import java.util.HashMap;
import java.util.Map;

public class MockTrie implements ITrie {
  public MockTrie() {
  }

  // Insert a word into the trie
  @Override
  public void insert(String word) {
    return;
  }

  // Search for a word in the trie
  @Override
  public boolean search(String word) {
    return false;
  }

  // Check if there is any word in the trie that starts with the given prefix
  @Override
  public boolean startsWith(String prefix) {
    return true;
  }

  @Override
  public boolean remove(String word){
    return false;
  }

  @Override
  public String getTitle() {
    return "Mock";
  }

}