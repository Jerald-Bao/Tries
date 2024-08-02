import java.util.HashMap;
import java.util.Map;

public class TrieHashMap implements ITrie{

  // define the TrieNode class
  private class TrieNode {
      // fields
      boolean isEndOfWord;
      Map<Character,TrieNode> children;

      // constructor
      public TrieNode(){
      children = new HashMap<>();
      }
  }

  // fields
  private TrieNode root;

  // constructor
  public TrieHashMap(){
      root = new TrieNode();
  }

  // methods
  @Override
  public void insert(String word) {
    TrieNode cur = root;
    for (char letter : word.toCharArray()) {
      cur = cur.children.computeIfAbsent(letter, k -> new TrieNode());
    }
    cur.isEndOfWord = true;
  }
  
  @Override
  public boolean search(String word) {
    TrieNode cur = root;
    for (char letter : word.toCharArray()) {
      if (cur.children.get(letter) == null) {
        return false;
      }
      cur = cur.children.get(letter);
    }
    return cur.isEndOfWord;
  }

  @Override
  public boolean startsWith(String prefix) {
    TrieNode cur = root;
    for (char letter : prefix.toCharArray()) {
      if (cur.children.get(letter) == null) {
        return false;
      }
      cur = cur.children.get(letter);
    }
    return true;
  }

  @Override
  public boolean remove(String word) {
      if (!search(word)) {
          return false;
      }
  
      TrieNode cur = root;
  
      for (int i = 0; i < word.length(); i++) {
          char letter = word.charAt(i);
          cur = cur.children.get(letter);
      }
  
      // if cur(word end node) is part of a longer word, then mark it as not the end of the word
      if (!cur.children.isEmpty()) {
          cur.isEndOfWord = false;
      } else {
          // if cur is the leaf node, then recursively delete nodes which are not end of word and have no children
          delete(word);
      }
      return true;
  }
  
  private void delete(String word) {
      TrieNode cur = root;
  
      // recursively delete nodes which are not end of word and have no children
      deleteHelper(cur, word, 0);
  }
  
  private boolean deleteHelper(TrieNode cur, String word, int i) {
      // base case
      if (i == word.length()) {
          cur.isEndOfWord = false;
          return cur.children.isEmpty();
      }
  
      char letter = word.charAt(i);
      TrieNode nextNode = cur.children.get(letter);
  
      if (nextNode == null) {
          return false;
      }
  
      // recursive case
      boolean shouldDeleteCurrentNode = deleteHelper(nextNode, word, i + 1);
  
      if (shouldDeleteCurrentNode) {
          cur.children.remove(letter);
          return cur.children.isEmpty() && !cur.isEndOfWord;
      }
  
      return false;
  }

  @Override
  public String getTitle() {
    return "TrieHashMap Implementation";
  }
}

