import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RadixTreeTest {

  private RadixTree radixTree;

  @BeforeEach
  public void setUp() {
    radixTree = new RadixTree();
    radixTree.insert("Milk");
    radixTree.insert("Mike");
    radixTree.insert("Machine");
    radixTree.insert("March");
  }

  @Test
  public void testSearch() {
    assertTrue(radixTree.search("Mike"));   // should return true
    assertFalse(radixTree.search("Make"));  // should return false
    assertTrue(radixTree.search("March"));  // should return true
  }

  @Test
  public void testStartsWith() {
    assertTrue(radixTree.startsWith("Ma"));  // should return true
    assertFalse(radixTree.startsWith("Mou")); // should return false
  }

  @Test
  public void testRemoveAndSearch() {
    assertTrue(radixTree.remove("Milk"));   // should return true
    assertFalse(radixTree.search("Milk"));  // should return false
    assertTrue(radixTree.search("Mike"));   // should return true
  }
}
