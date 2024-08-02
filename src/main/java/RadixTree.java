import java.util.HashMap;
import java.util.Map;

public class RadixTree implements ITrie{

  // define a Node
  private class Node {
    // fields
    String key;
    Map<Character, Node> children;

    // constructor
    public Node(String key) {
      this.key = key;
      this.children = new HashMap<>();
    }
  }

  private final Node root = new Node("");

  @Override
  public void insert(String word) {
    insert(word, root);
  }

  // insert words in order of "carrie" and "cat" for example
  private void insert(String word, Node node) {
    int i = 0;
    // find the prefix "ca"
    while (i < word.length() && i < node.key.length() && word.charAt(i) == node.key.charAt(i)) {
      i++;
    }

    // split the key at the mismatch point "ca" and add new node "rrie"
    if (i < node.key.length()) {
      Node child = new Node(node.key.substring(i));
      child.children.putAll(node.children);
      node.children.clear();
      node.children.put(child.key.charAt(0), child);
      node.key = node.key.substring(0, i);
    }

    // add the remaining part of character "t"
    // because index i("ca" = 2) is less than the word length("cat" = 3)
    if (i < word.length()) {
      String remaining = word.substring(i);
      Node child = node.children.get(remaining.charAt(0));
      if (child == null) {
        node.children.put(remaining.charAt(0), new Node(remaining));
      } else {
        insert(remaining, child);
      }
    }
  }

  @Override
  public boolean search(String word) {
    return search(word, root);
  }

  private boolean search(String word, Node node) {
    if (word.equals(node.key)) {
      return true;
    }

    if (word.startsWith(node.key)) {
      // check if the next character is in the children map
      // if the key is "ca", this key.length() is 2,
      // which is the index of the next character
      if (node.children.containsKey(word.charAt(node.key.length()))) {
        // recursively call the search function
        // search the remaining part of the word
        return search(word.substring(node.key.length()), node.children.get(word.charAt(node.key.length())));
      }
    }
    return false;
  }

  @Override
  public boolean startsWith(String prefix) {
    Node currentNode = root;
    for (char ch : prefix.toCharArray()) {
      currentNode = currentNode.children.get(ch);
      if (currentNode == null) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean remove(String word) {
    return delete(word, root, null, '\0');
  }

  private boolean delete(String word, Node node, Node parent, char parentKey) {
    // check if the word is the same as the key
    if (word.equals(node.key)) {
      if (node.children.isEmpty()) {
        if (parent != null) {
          parent.children.remove(parentKey);
        }
        return true;
      } else if (node.children.size() == 1) {
        Node child = node.children.values().iterator().next();
        node.key += child.key;
        node.children = child.children;
        return true;
      }
      // check if the word is start with the key
      // if word is "carrie" and "c" is node.key
    } else if (word.startsWith(node.key)) {
      // check the next node
      char nextKey = word.charAt(node.key.length());
      Node child = node.children.get(nextKey);
      if (child != null) {
        return delete(word.substring(node.key.length()), child, node, nextKey);
      }
    }
    return false;
  }
}