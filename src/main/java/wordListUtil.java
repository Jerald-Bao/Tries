import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class wordListUtil {
  public static List<String> LoadFromFile(String fileName, int wordCount) {
    List<String> words = new ArrayList<>();
    Random rnd = new Random(42);
    // Read all words from the file
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String word;
      while ((word = br.readLine()) != null) {
        words.add(word.toLowerCase()); // Convert to lowercase to ensure consistency
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Shuffle the list to randomize the order
    Collections.shuffle(words,rnd);

    return words.subList(0,wordCount);
  }

  public static List<String> generateRandomWords(int numWords, int wordLengthMax) {
    List<String> words = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < numWords; i++) {
      int nextWordLength = random.nextInt(10);
      StringBuilder sb = new StringBuilder(nextWordLength);
      for (int j = 0; j < nextWordLength; j++) {
        sb.append((char) ('a' + random.nextInt(26)));
      }
      words.add(sb.toString());
    }
    return words;
  }
}
