public class TrieArray implements ITrie{

  // define a TrieNode
  private class TrieNode {
    
    // fields
    private TrieNode[] children;
    private boolean isEndOfWord;

    // constructor
    public TrieNode() {
      children = new TrieNode[26];
      isEndOfWord = false;
    }
  }

  // fields
  private TrieNode root;

  // constructor
  public TrieArray() {
    root = new TrieNode();
  }

  // methods
  @Override
  public void insert(String word) {
    TrieNode cur = root;

    for (int i = 0; i < word.length(); i++){

      int index = word.charAt(i) - 'a';
      if (cur.children[index] == null) {
        cur.children[index] = new TrieNode();
      }
      cur = cur.children[index];
    }
    cur.isEndOfWord = true;
  }

  @Override
  public boolean search(String word) {
    TrieNode cur = root;

    for (int i = 0; i < word.length(); i++) {
      int index = word.charAt(i) - 'a';
      if (cur.children[index] == null) {
        return false;
      }
      cur = cur.children[index];
    }
    return cur.isEndOfWord;
  }

  @Override
  public boolean startsWith(String prefix) {
    TrieNode cur = root;

    for (int i = 0; i < prefix.length(); i++) {
      int index = prefix.charAt(i) - 'a';
      if (cur.children[index] == null) {
        return false;
      }
      cur = cur.children[index];
    }
    return true;
  }

  @Override
  public boolean remove(String word) {

    // if the word doesn't exit in the trie, then return
    if (!search(word)) {
      return false;
    }

    // make a pointer pointing to the word end
    TrieNode cur = root;

    for (int i = 0; i < word.length(); i++) {
      int index = word.charAt(i) - 'a';
      cur = cur.children[index];
    }

    // if cur(word end node) is part of a longer word, then mark it as not the end of the word
    if (!isChildrenEmpty(cur.children)) {
      cur.isEndOfWord = false;
    } // if cur is the leaf node, then recursively delete nodes which are not end of word and have no children
    else {
      delete(word);
    }
    return true;
  }

  private void delete(String word) {
    TrieNode cur = root;

    // recursively delete nodes which are not end of word and have no children
    deleteHelper(cur, word, 0);
  }

  private void deleteHelper(TrieNode cur, String word, int i) {

    // base case
    if (i == word.length() - 1) {
      cur.children[word.charAt(i) - 'a'] = null;
      return;
    }

    // recursive case
    int index = word.charAt(i) - 'a';
    deleteHelper(cur.children[index], word, i + 1);
    if (!isChildrenEmpty(cur.children) || cur.children[index].isEndOfWord) {
      return;
    }
    cur.children[index] = null;
    return;
  }

  private boolean isChildrenEmpty(TrieNode[] children) {
    for (TrieNode child : children) {
        if (child != null) {
            return false;
        }
    }
    return true;
  }

}
