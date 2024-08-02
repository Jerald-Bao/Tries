import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Cleaner {

    public static void main(String[] args) {
        String inputFilePath = "src/main/resources/wordlist.txt";
        String outputFilePath = "src/main/resources/cleaned_wordlist.txt";

        try {
            // Read the file
            BufferedReader reader = new BufferedReader(new FileReader(inputFilePath));
            StringBuilder cleanedContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Filter out non-lowercase alphabetic characters
                for (char c : line.toCharArray()) {
                    if (c >= 'a' && c <= 'z') {
                        cleanedContent.append(c);
                    }
                }
                cleanedContent.append(System.lineSeparator());
            }
            reader.close();

            // Write the cleaned content to a new file
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write(cleanedContent.toString());
            writer.close();

            System.out.println("Cleaning completed. Cleaned file saved as " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}