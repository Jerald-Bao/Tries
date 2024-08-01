import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TrieTestLargeScale {

    private ITrie trie;

    @BeforeEach
    public void setUp() {
        trie = new PatriciaTrie();
    }

    @Test
    public void testInsertAndSearch() {
        int n=100;
        List<String> wordsInsert = wordListUtil.LoadFromFile("src/main/resources/wordlist.txt",n );
        List<String> wordsSearchFalse = wordListUtil.generateRandomWords(n,10);
        wordsSearchFalse.removeIf(wordsInsert::contains);

        for (int i=0;i<n;i++) {
            trie.insert(wordsInsert.get(i));
            for (int j=0;j<=i;j++) {
                if (!trie.search(wordsInsert.get(j))){
                    fail();
                }
            }
        }
        for (String word : wordsSearchFalse) {
            if (trie.search(word))
                fail();
        }
    }
}
