import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrieHashMapTest {

    private TrieHashMap trie;

    @BeforeEach
    public void setUp() {
        trie = new TrieHashMap();
    }

    @Test
    public void testInsertAndSearch() {
        trie.insert("apple");
        assertTrue(trie.search("apple"));   // should return true
        assertFalse(trie.search("app"));    // should return false
    }

    @Test
    public void testStartsWith() {
        trie.insert("apple");
        assertTrue(trie.startsWith("app")); // should return true
        assertFalse(trie.startsWith("apl")); // should return false
    }

    @Test
    public void testRemove() {
        trie.insert("apple");
        assertTrue(trie.search("apple"));   // should return true
        trie.remove("apple");
        assertFalse(trie.search("apple"));  // should return false
        assertFalse(trie.startsWith("app")); // should return false
    }

    @Test
    public void testRemovePrefix() {
        trie.insert("apple");
        trie.insert("app");
        assertTrue(trie.search("apple"));   // should return true
        assertTrue(trie.search("app"));     // should return true
        trie.remove("app");
        assertTrue(trie.search("apple"));   // should return true
        assertFalse(trie.search("app"));    // should return false
    }

    @Test
    public void testInsertAndRemoveMultipleWords() {
        trie.insert("apple");
        trie.insert("app");
        trie.insert("apricot");
        trie.insert("banana");

        assertTrue(trie.search("apple"));
        assertTrue(trie.search("app"));
        assertTrue(trie.search("apricot"));
        assertTrue(trie.search("banana"));

        trie.remove("apple");
        assertFalse(trie.search("apple"));
        assertTrue(trie.search("app"));
        assertTrue(trie.search("apricot"));
        assertTrue(trie.search("banana"));

        trie.remove("app");
        assertFalse(trie.search("app"));
        assertTrue(trie.search("apricot"));
        assertTrue(trie.search("banana"));

        trie.remove("apricot");
        assertFalse(trie.search("apricot"));
        assertTrue(trie.search("banana"));

        trie.remove("banana");
        assertFalse(trie.search("banana"));
    }
}