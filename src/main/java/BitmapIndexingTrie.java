import java.util.ArrayList;

public class BitmapIndexingTrie implements ITrie {

  class TrieNode {
    ArrayList<TrieNode> children;
    boolean isWord;
    Long bitmap;

    public TrieNode() {
      children = new ArrayList<TrieNode>();
      isWord = false;
      bitmap = 0L;
    }

    public int getBitmapIndex(char c){
      return (c - 'a');
    }

    public void setChild(char c) {
      bitmap |= (1L << (Long.SIZE - getBitmapIndex(c) - 1));
    }

    public void removeChild(char c) {
      long mask = ~0L & ~(1L << (Long.SIZE - getBitmapIndex(c) - 1));
      bitmap &= mask;
    }

    public boolean hasChild(char c) {
      return (bitmap & (1L << (Long.SIZE - getBitmapIndex(c) - 1))) != 0;
    }

    public TrieNode getChildNode(char c) {
      if (hasChild(c))
        return children.get(childrenBehind(this.bitmap, getBitmapIndex(c)));
      return null;
    }

    public void setChildNode(char c, TrieNode newNode) {
      if (hasChild(c))
        children.set(childrenBehind(this.bitmap, getBitmapIndex(c)), newNode);
      else {
        setChild(c);
        children.add(childrenBehind(this.bitmap, getBitmapIndex(c)), newNode);
      }
    }

    public void removeChildNode(char c) {
      if (hasChild(c)) {
        children.remove(childrenBehind(this.bitmap, getBitmapIndex(c)));
        removeChild(c);
      }
    }

    private int childrenBehind(Long bitmap, int fromIndex) {
      return Long.bitCount(bitmap<<(fromIndex + 1));
    }
  }

  private TrieNode root;

  public BitmapIndexingTrie() {
    this.root = new TrieNode();
  }

  @Override
  public void insert(String word) {
    TrieNode node = root;
    for (char c : word.toCharArray()) {
      if (!node.hasChild(c)) {
        TrieNode newNode = new TrieNode();
        node.setChildNode(c,newNode);
        node = newNode;
      } else node = node.getChildNode(c);
    }
    node.isWord = true;
  }

  @Override
  public boolean search(String word) {
    TrieNode node = root;
    for (char c : word.toCharArray()) {
      if (!node.hasChild(c)) {
        return false;
      } else node = node.getChildNode(c);
    }
    return node.isWord;
  }

  @Override
  public boolean startsWith(String prefix) {
    TrieNode node = root;
    for (char c : prefix.toCharArray()) {
      if (!node.hasChild(c)) {
        return false;
      } else node = node.getChildNode(c);
    }
    return true;
  }

  @Override
  public boolean remove(String word) {
    return remove(root, word, 0);
  }

  private boolean remove(TrieNode node, String word, int depth) {
    if (depth == word.length()) {
      if (!node.isWord) {
        return false;
      }
      node.isWord = false;
      return true; // If no children, node can be deleted
    }

    char c = word.charAt(depth);
    TrieNode childNode = node.getChildNode(c);
    if (childNode == null) {
      return false;
    }

    if (remove(childNode, word, depth + 1)) {
      if (childNode.bitmap == 0 && !node.isWord) {
        node.removeChildNode(c);
      }
      return true;
    }
    return false;
  }


  @Override
  public String getTitle() {
    return "Bitmap Trie";
  }
}