import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.jfree.chart.ui.ApplicationFrame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
A analysis script that evaluate the performance of different variations of tries in different scales
 */
public class TriePerformanceAnalyser extends ApplicationFrame {

  private static final int NUM_WORDS = 100000;
  private static final int WORD_LENGTH = 10;

  public TriePerformanceAnalyser(String title,ITrie trie) {
    super(title);
    JFreeChart barChart = ChartFactory.createBarChart(
        "Trie Performance Analysis",
        "Operation",
        "Time (nanoseconds)",
        createDataset(),
        PlotOrientation.VERTICAL,
        true, true, false);
    CategoryPlot plot = barChart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setSeriesPaint(0, new Color(200,80,80));
    renderer.setSeriesPaint(1, new Color(80,200,80));
    renderer.setSeriesPaint(2, new Color(80,80,200));
    renderer.setDrawBarOutline(false);
    renderer.setShadowVisible(false); // Remove shadows
    renderer.setBarPainter(new StandardBarPainter());
    ChartPanel chartPanel = new ChartPanel(barChart);
    chartPanel.setPreferredSize(new Dimension(800, 600));
    setContentPane(chartPanel);
  }

  // Force the system to execute GC to recycle temporary memory usage
  private void forceGarbageCollection(){
    System.gc();

    // Give some time for GC to run
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private CategoryDataset createDataset() {
    final String INSERT = "Insert";
    final String SEARCH = "Search";
    final String SPACE = "Memory Usage";
    ITrie trie = new Trie();
    ITrie mockTrie = new MockTrie();
    int n = 1000;

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    List<String> wordsInsert = LoadFromFile("src/main/resources/wordlist.txt",n );
    List<String> wordsSearch = LoadFromFile("src/main/resources/wordlist.txt",n);



    try (PerformanceMonitor monitor = new PerformanceMonitor()) {
      for (String word : wordsInsert) {
        trie.insert(word);
      }
      dataset.addValue(monitor.getRunningTime(), INSERT, INSERT);

      for (String word : wordsSearch) {
        trie.search(word);
      }
      dataset.addValue(monitor.getRunningTime(), SEARCH, SEARCH);
      dataset.addValue(monitor.getMemoryUsage(), SPACE, SPACE);
    } catch (Exception e) {
      System.err.println("An exception is thrown during the performance testing");
      System.err.println(e.getMessage());
    }



    return dataset;
  }

  private List<String> generateRandomWords(int numWords, int wordLength) {
    List<String> words = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < numWords; i++) {
      StringBuilder sb = new StringBuilder(wordLength);
      for (int j = 0; j < wordLength; j++) {
        sb.append((char) ('a' + random.nextInt(26)));
      }
      words.add(sb.toString());
    }
    return words;
  }

  private List<String> LoadFromFile(String fileName, int wordCount) {
    List<String> words = new ArrayList<>();

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
    Collections.shuffle(words);

    return words.subList(0,wordCount);
  }

  public static void main(String[] args) {
    TriePerformanceAnalyser chart = new TriePerformanceAnalyser("Trie Performance Analysis", new MockTrie());
    chart.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = chart.getWidth();
    int height = chart.getHeight();
    int x = (screenSize.width - width) / 2;
    int y = (screenSize.height - height) / 2;
    chart.setLocation(x, y);
    chart.setVisible(true);
    chart.setVisible(true);
  }

  public class PerformanceMonitor implements AutoCloseable{
    long startTime = 0;
    long startMemoryUsage = 0;
    public PerformanceMonitor(){
      forceGarbageCollection();
      startTime = System.nanoTime();
      startMemoryUsage = getTotalMemoryUsage();
    }

    private long getTotalMemoryUsage(){
      Runtime runtime = Runtime.getRuntime();
      // Calculate the used memory
      long memoryUsed = runtime.totalMemory() - runtime.freeMemory();
      return memoryUsed;
    }

    public long getMemoryUsage(){
      return getTotalMemoryUsage() - startMemoryUsage;
    }

    public long getRunningTime(){
      return System.nanoTime()-startTime;
    }

    @Override
    public void close() throws Exception {
    }
  }

}