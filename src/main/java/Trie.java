import java.util.HashMap;
import java.util.Map;
public class Trie implements ITrie {

  private TrieNode root;

  public Trie() {
    root = new TrieNode();
  }

  // Insert a word into the trie
  @Override
  public void insert(String word) {
    TrieNode current = root;
    for (char ch : word.toCharArray()) {
      current = current.children.computeIfAbsent(ch, c -> new TrieNode());
    }
    current.isEndOfWord = true;
  }

  // Search for a word in the trie
  @Override
  public boolean search(String word) {
    TrieNode current = root;
    for (char ch : word.toCharArray()) {
      TrieNode node = current.children.get(ch);
      if (node == null) {
        return false;
      }
      current = node;
    }
    return current.isEndOfWord;
  }

  // Check if there is any word in the trie that starts with the given prefix
  @Override
  public boolean startsWith(String prefix) {
    TrieNode current = root;
    for (char ch : prefix.toCharArray()) {
      TrieNode node = current.children.get(ch);
      if (node == null) {
        return false;
      }
      current = node;
    }
    return true;
  }

  @Override
  public boolean remove(String word){
    //todo
    return false;
  }

  private class TrieNode {
    private Map<Character, TrieNode> children;
    private boolean isEndOfWord;

    public TrieNode() {
      children = new HashMap<>();
      isEndOfWord = false;
    }
  }

  @Override
  public String getTitle() {
    return "Trie";
  }
}