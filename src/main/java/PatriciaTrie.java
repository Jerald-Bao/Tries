public class PatriciaTrie implements ITrie {
  private static class TrieNode {
    String word;
    public long[] bitArray;

    TrieNode left = null;
    TrieNode right = null;

    int skipNum;
    boolean isWord;

    TrieNode() {
      this.word = null;
      skipNum = 0;
    }
    TrieNode(long[] bitArray, int skipNum) {
      this.bitArray = bitArray;
      this.skipNum = skipNum;
    }
    TrieNode(long[] bitArray, int skipNum,TrieNode left, TrieNode right) {
      this.bitArray = bitArray;
      this.skipNum = skipNum;
      this.left = left;
      this.right =right;
    }


  }

  private final TrieNode root;

  public PatriciaTrie() {
    root = new TrieNode();
  }

  @Override
  public void insert(String word) {
    long[] remainingBitArray = compress(word.getBytes());
    int remainingBits = word.length() * Byte.SIZE;
    TrieNode node = root;
    if (node.left ==null && node.right == null && node.skipNum == 0) {
      node.skipNum = remainingBits;
      node.bitArray = remainingBitArray;
      node.isWord = true;
    }
    while (true) {
      int matchResult = match(node, remainingBitArray, remainingBits);
      if (remainingBits < node.skipNum  && matchResult == -1) {
        long[] commonPrefix = subFirstNBits(node.bitArray,remainingBits);
        leftShift(node.bitArray, remainingBits);
        node.bitArray = trimTail(node.bitArray,node.skipNum - remainingBits);
        boolean newEdge = (remainingBitArray[0] & (0x1L << 63)) != 0x0L;
        leftShift(node.bitArray, 1);
        if (!newEdge) {
          node.left = new TrieNode(node.bitArray,node.skipNum - remainingBits-1,node.left,node.right);
        }
        else {
          node.right = new TrieNode(node.bitArray,node.skipNum - remainingBits-1,node.left,node.right);
        }
        node.bitArray = commonPrefix;
        node.skipNum = remainingBits;
        node.isWord = true;
        return;
      }
      if (matchResult == -1) {
        if (remainingBits > node.skipNum) {
          leftShift(remainingBitArray, node.skipNum);
          remainingBits -= node.skipNum+1;
          boolean nextEdge = (remainingBitArray[0] & (0x1L << 63)) != 0x0L;
          leftShift(remainingBitArray, 1);
          if (nextEdge) {
            if (node.right == null) {
              node.right = new TrieNode(remainingBitArray, remainingBits);
              node.right.isWord = true;
              return;
            }
            node = node.right;
          } else {
            if (node.left == null) {
              node.left = new TrieNode(remainingBitArray, remainingBits);
              node.left.isWord = true;
              return;
            }
            node = node.left;
          }
          continue;
        } else {
          node.isWord = true;
          return;
        }
      }

      // otherwise, branch here
      long[] commonPrefix = subFirstNBits(node.bitArray, matchResult);
      leftShift(remainingBitArray, matchResult);
      leftShift(node.bitArray, matchResult);
      boolean newEdgeOldBranch = (node.bitArray[0] & (0x1L << 63)) != 0x0L;
      leftShift(node.bitArray, 1);
      node.bitArray = trimTail(node.bitArray,node.skipNum - matchResult);
      if (!newEdgeOldBranch) {
        node.left = new TrieNode(node.bitArray,node.skipNum - matchResult-1,node.left,node.right);
      }
      else {
        node.right = new TrieNode(node.bitArray,node.skipNum - matchResult-1,node.left,node.right);
      }

      boolean newEdgeWord = (remainingBitArray[0] & (0x1L << 63)) != 0x0L;
      leftShift(remainingBitArray, 1);
      if (!newEdgeWord) {
        node.left = new TrieNode(remainingBitArray,remainingBits - matchResult - 1);
        node.left.isWord = true;
      }
      else {
        node.right = new TrieNode(remainingBitArray,remainingBits - matchResult - 1);
        node.right.isWord = true;
      }



      node.bitArray = commonPrefix;
      node.skipNum = matchResult;
      return;
    }

  }

  // compress a byte[] bit array into a long[] array
  public static long[] compress(byte[] byteArray) {
    int longArrayLength = (byteArray.length - 1) / 8 + 1;
    long[] longArray = new long[longArrayLength];

    for (int i = 0; i < longArrayLength; i++) {
      long value = 0;
      for (int j = 0; j < 8; j++) {
        int byteIndex = i * 8 + j;
        if (byteIndex < byteArray.length) {
          value |= (((long) byteArray[byteIndex] & 0xFFL)<<56) >>> (8 * j);
        }
      }
      longArray[i] = value;
    }

    return longArray;
  }

  public static void leftShift(long[] array, int shiftAmount) {
    if (shiftAmount < 0 || array == null || array.length == 0) {
      return; // Invalid shift amount or empty array
    }

    int fullShift = shiftAmount / Long.SIZE; // Number of full long shifts
    int bitShift = shiftAmount % Long.SIZE; // Remaining bit shift within a long

    // Handle full long shifts
    if (fullShift > 0) {
      for (int i = 0; i < array.length; i++) {
        if (i + fullShift < array.length) {
          array[i] = array[i + fullShift];
        } else {
          array[i] = 0;
        }
      }
    }
    // Handle remaining bit shifts
    if (bitShift > 0) {
      for (int i = 0; i < array.length-1; i++) {
        long carry = (array[i+1] >>> (Long.SIZE - bitShift)) & ((1L << bitShift) - 1);
        array[i] = (array[i] << bitShift) | carry;
      }
      array[array.length-1] = (array[array.length-1] << bitShift) ;
    }
  }

  public static long[] subFirstNBits(long[] bitArray, int n) {
    if (n <= 0 || bitArray == null || bitArray.length == 0) {
      return new long[0]; // Return an empty array if n is non-positive or bitArray is null/empty
    }

    int longSize = 64; // Number of bits in a long
    int fullLongs = n / longSize; // Full long elements required
    int remainingBits = n % longSize; // Remaining bits after full longs

    // The number of longs needed to store the first n bits
    int neededLongs = remainingBits > 0 ? fullLongs + 1 : fullLongs;
    long[] result = new long[neededLongs];

    for (int i = 0; i < fullLongs; i++) {
      result[i] = bitArray[i];
    }

    if (remainingBits > 0) {
      // Extract the remaining bits from the next long, if it exists
      if (fullLongs < bitArray.length) {
        long mask = (0xFFFFFFFFFFFFFFFFL << (longSize - remainingBits)) ; // Mask to isolate the last bits
        result[fullLongs] = bitArray[fullLongs] & mask;
      }
    }

    return result;
  }

  private static int match(TrieNode node, long[] remainingBitArray, int remainingBits)
  {
    int matchingBits= Math.min(node.skipNum,remainingBits);
    if (matchingBits == 0)
      return -1;
    //compare first a few whole chunks of bits.
    int lastIndex = matchingBits / Long.SIZE;
    int i;
    boolean mismatch = false;
    for (i=0; i<lastIndex; i++) {
      if ((remainingBitArray[i] ^ node.bitArray[i]) != 0) {
        mismatch = true;
        break;
      }
    }
    int remainingBits2Compare;
    if (!mismatch) {
      remainingBits2Compare = matchingBits % Long.SIZE;
      if (remainingBits2Compare!= 0)
        //compare first a couple bits. Mask rest of bits with 0.
        if (((remainingBitArray[lastIndex] ^ node.bitArray[lastIndex])
            & (0x8000000000000000L >> (remainingBits2Compare-1))) != 0L)
          mismatch = true;
    }
    else {
      remainingBits2Compare = Long.SIZE;
    }
    if (!mismatch)
      return -1;
    long bitChunkWord =remainingBitArray[i];
    long bitChunkNode =node.bitArray[i];
    for (int j=0; j<remainingBits2Compare; j++) {
      if ((bitChunkWord>>>63 ^ bitChunkNode>>>63) == 1L)
        return i*Long.SIZE + j;
      bitChunkWord <<=1;
      bitChunkNode <<=1;
    }
    throw new IllegalStateException("Should've found diverging point of two mismatching bitarrays");
  }

  private static long[] trimTail(long[] bitArray,int remainingBits){
    int newSize = (remainingBits - 1) / Long.SIZE +1;
    if (newSize == bitArray.length)
      return bitArray;
    long[] newArray=new long[newSize];
    for (int i=0; i<newArray.length;i++)
    {
      newArray[i] = bitArray[i];
    }
    return newArray;
  }

  @Override
  public boolean search(String word) {
    long[] remainingBitArray = compress(word.getBytes());
    int remainingBits = word.length() * Byte.SIZE;
    TrieNode node = root;
    while (true) {
      if (remainingBits < node.skipNum){
        return false;
      }
      if (remainingBits > node.skipNum) {
        int matchResult = match(node, remainingBitArray,remainingBits);
        if (matchResult != -1) {
          return false;
        }
        leftShift(remainingBitArray, node.skipNum);
        remainingBits -= node.skipNum;
        boolean newEdge = (remainingBitArray[0] & (0x1L << 63)) != 0x0L;
        if (!newEdge)
          node = node.left;
        else
          node = node.right;

        leftShift(remainingBitArray, 1);
        remainingBits--;
      }
      else {
        int matchResult = match(node, remainingBitArray,remainingBits);
        return (matchResult == -1);
      }


    }
  }

  @Override
  public boolean startsWith(String prefix) {
    return true;
  }

  @Override
  public boolean remove(String word) {
    return remove(root, word);
  }

  private boolean remove(TrieNode node, String word) {
    return false;
  }

  @Override
  public String getTitle() {
    return "Patricia Trie";
  }
}
