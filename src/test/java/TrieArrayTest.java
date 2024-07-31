import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrieArrayTest {

    private TrieArray trie;

    @BeforeEach
    public void setUp() {
        trie = new TrieArray();
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
    public void testInsertAndSearchMultipleWords() {
        trie.insert("apple");
        trie.insert("app");
        assertTrue(trie.search("apple"));   // should return true
        assertTrue(trie.search("app"));     // should return true
        assertFalse(trie.search("appl"));   // should return false
    }

    @Test
    public void testRemoveWord() {
        trie.insert("apple");
        trie.insert("app");
        assertTrue(trie.remove("apple"));   // should return true
        assertFalse(trie.search("apple"));  // should return false
        assertTrue(trie.search("app"));     // should return true
    }

    @Test
    public void testRemoveWordNotInTrie() {
        trie.insert("apple");
        assertFalse(trie.remove("appl"));   // should return false
        assertTrue(trie.search("apple"));   // should return true
    }

    @Test
    public void testRemovePrefix() {
        trie.insert("app");
        trie.insert("apple");
        assertTrue(trie.remove("app"));     // should return true
        assertFalse(trie.search("app"));    // should return false
        assertTrue(trie.search("apple"));   // should return true
    }

    @Test
    public void testRemoveAndCheckPrefix() {
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        assertTrue(trie.remove("apple"));     // should return true
        assertFalse(trie.search("apple"));    // should return false
        assertTrue(trie.search("app"));       // should return true
        assertTrue(trie.search("application"));// should return true
        assertTrue(trie.startsWith("app"));   // should return true
    }

    @Test
    public void testInsertAndSearchEdgeCases() {
        trie.insert("art");
        trie.insert("andy");
        trie.insert("and");
        trie.insert("an");
        trie.insert("andrea");

        assertTrue(trie.search("art"));       // should return true
        assertTrue(trie.search("andy"));      // should return true
        assertTrue(trie.search("and"));       // should return true
        assertTrue(trie.search("an"));        // should return true
        assertTrue(trie.search("andrea"));    // should return true

        assertFalse(trie.search("ar"));       // should return false
        assertFalse(trie.search("andre"));    // should return false
        assertFalse(trie.search("andreas"));  // should return false
    }

    @Test
    public void testStartsWithEdgeCases() {
        trie.insert("art");
        trie.insert("andy");
        trie.insert("and");
        trie.insert("an");
        trie.insert("andrea");

        assertTrue(trie.startsWith("a"));     // should return true
        assertTrue(trie.startsWith("an"));    // should return true
        assertTrue(trie.startsWith("and"));   // should return true
        assertTrue(trie.startsWith("andr"));  // should return true
        assertTrue(trie.startsWith("art"));   // should return true

        assertFalse(trie.startsWith("b"));    // should return false
        assertFalse(trie.startsWith("arx"));  // should return false
        assertFalse(trie.startsWith("andx")); // should return false
    }
}
