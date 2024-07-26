public interface ITrie {
  // Insert a word into the trie
  void insert(String word);

  // Search for a word in the trie
  boolean search(String word);

  // Check if there is any word in the trie that starts with the given prefix
  boolean startsWith(String prefix);
  boolean remove(String word);

}
