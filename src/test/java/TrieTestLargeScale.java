import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
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
        int n=10000;
        List<String> wordsInsert = wordListUtil.LoadFromFile("src/main/resources/wordlist.txt",n );
        List<String> wordsSearchFalse = wordListUtil.generateRandomWords(n,10);

        wordsSearchFalse.removeIf(wordsInsert::contains);

        List<String> wordsDelete = new ArrayList<>(wordsInsert);
        Collections.shuffle(wordsDelete);
        wordsDelete.subList(0,n/10);

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
        for (String word : wordsDelete) {
            if (!trie.remove(word))
                fail();
        }
        for (String word : wordsInsert) {
            if (wordsDelete.contains(word)) {
                if (trie.search(word))
                    fail();
            }
            else if (!trie.search(word))
                fail();
        }
    }
}
