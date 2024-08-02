public class PatriciaTrie implements ITrie {
  private static class TrieNode {
    public long[] bitArray;

    TrieNode left = null;
    TrieNode right = null;

    int skipNum;
    boolean isWord;

    TrieNode() {
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
    // convert word to bit array.
    long[] remainingBitArray = compress(word.getBytes());
    int remainingBits = word.length() * Byte.SIZE;
    TrieNode node = root;
    // if the trie is empty:
    if (node.left ==null && node.right == null && node.skipNum == 0) {
      node.skipNum = remainingBits;
      node.bitArray = remainingBitArray;
      node.isWord = true;
    }
    while (true) {
      int matchResult = match(node, remainingBitArray, remainingBits);

      // if the inserted word is part of the current skip prefix, diverge the node
      if (remainingBits < node.skipNum  && matchResult == -1) {
        long[] commonPrefix = subFirstNBits(node.bitArray,remainingBits);
        leftShift(node.bitArray, remainingBits);
        leftShift(remainingBitArray, remainingBits);

        // trim the node bit array to keep the trie as minimal size.
        node.bitArray = trimTail(node.bitArray,node.skipNum - remainingBits);

        // extract the edge-determining bit.
        boolean newEdge = (node.bitArray[0] & (0x1L << 63)) != 0x0L;
        leftShift(node.bitArray, 1);

        // create the branch and attach the original node to the branch
        if (!newEdge) {
          node.left = new TrieNode(node.bitArray,node.skipNum - remainingBits-1,node.left,node.right);
          node.left.isWord = node.isWord;
        }
        else {
          node.right = new TrieNode(node.bitArray,node.skipNum - remainingBits-1,node.left,node.right);
          node.right.isWord = node.isWord;
        }
        node.bitArray = commonPrefix;
        node.skipNum = remainingBits;
        node.isWord = true;
        return;
      }
      if (matchResult == -1) {

        // if prefix matches and it isn't the end, we visit the next node.
        if (remainingBits > node.skipNum) {

          leftShift(remainingBitArray, node.skipNum);
          remainingBits -= node.skipNum+1;

          // extract the edge-determining bit.
          boolean nextEdge = (remainingBitArray[0] & (0x1L << 63)) != 0x0L;
          leftShift(remainingBitArray, 1);
          if (nextEdge) {

            // if there's no node that we suppose to search into, create one as a leaf node.
            if (node.right == null) {
              node.right = new TrieNode(remainingBitArray, remainingBits);
              node.right.isWord = true;
              return;
            }
            node = node.right;
          } else {

            // same as above.
            if (node.left == null) {
              node.left = new TrieNode(remainingBitArray, remainingBits);
              node.left.isWord = true;
              return;
            }
            node = node.left;
          }
          continue;
        } else {

          // if the skip prefix is exactly the word, mark the prefix node as a word node
          node.isWord = true;
          return;
        }
      }

      // otherwise, then the skip prefix doesn't match the word.
      // extract the common prefix they share, make it the new common parent.
      long[] commonPrefix = subFirstNBits(node.bitArray, matchResult);
      leftShift(remainingBitArray, matchResult);
      leftShift(node.bitArray, matchResult);
      boolean newEdgeOldBranch = (node.bitArray[0] & (0x1L << 63)) != 0x0L;
      leftShift(node.bitArray, 1);
      node.bitArray = trimTail(node.bitArray,node.skipNum - matchResult);

      // at the diverge point, first attach the old branch to the newly created parent node
      if (!newEdgeOldBranch) {
        node.left = new TrieNode(node.bitArray,node.skipNum - matchResult-1,node.left,node.right);
        node.left.isWord = node.isWord;
      }
      else {
        node.right = new TrieNode(node.bitArray,node.skipNum - matchResult-1,node.left,node.right);
        node.right.isWord = node.isWord;
      }

      // next add the new word as a leaf node.
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

      // the diverging point is not a word.
      node.isWord = false;
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

  // left shift the entire bit array.
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

  // get the first N bits of the entire bit array.
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

  // match bit array A and B, if one of these is a prefix of the other, return -1;
  // otherwise, return the maximum common prefix length.
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

  // create a new trimmed bit array, to release memory usage.
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
      // if the current node was not created, but the word isn't consumed.
      // the word is not in the trie.
      if (node == null)
        return false;

      // if the prefix is longer than the current word.
      if (remainingBits < node.skipNum){
        return false;
      }
      if (remainingBits > node.skipNum) {
        int matchResult = match(node, remainingBitArray,remainingBits);
        // if the prefix is shorter, but it doesn't match the prefix.
        if (matchResult != -1) {
          return false;
        }
        // otherwise, visit next node.
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
        // if the prefix is exactly the length of the word remaining,
        // check if they match and if it's marked a word node.
        int matchResult = match(node, remainingBitArray,remainingBits);
        return (matchResult == -1) && node.isWord;
      }
    }
  }

  // Basically the same logic as searching
  @Override
  public boolean startsWith(String prefix) {
    long[] remainingBitArray = compress(prefix.getBytes());
    int remainingBits = prefix.length() * Byte.SIZE;
    TrieNode node = root;
    while (true) {
      if (node == null)
        return false;
      if (remainingBits < node.skipNum){
        return match(node, remainingBitArray,remainingBits) == -1;
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
  public boolean remove(String word) {
    long[] remainingBitArray = compress(word.getBytes());
    int remainingBits = word.length() * Byte.SIZE;
    return remove(root, remainingBitArray, remainingBits);
  }

  // find the leaf node and recursively delete empty nodes from bottom to the top.
  private boolean remove(TrieNode node, long[] remainingBitArray,int remainingBits) {
    if (node == null)
      return false;
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
      TrieNode nextNode;
      if (!newEdge)
        nextNode = node.left;
      else
        nextNode = node.right;
      leftShift(remainingBitArray, 1);
      remainingBits--;
      boolean res = remove(nextNode,remainingBitArray,remainingBits);
      // remove empty nodes
      if (nextNode !=null && !nextNode.isWord && nextNode.left == null && nextNode.right == null) {
        if (!newEdge){
          node.left = null;
        } else {
          node.right = null;
        }
      }
      return res;
    }
    else {
      int matchResult = match(node, remainingBitArray,remainingBits);
      node.isWord = false;
      return (matchResult == -1);
    }
  }

  @Override
  public String getTitle() {
    return "Patricia Trie";
  }
}
