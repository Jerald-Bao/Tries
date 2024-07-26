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
        createDataset(trie),
        PlotOrientation.VERTICAL,
        true, true, false);
    CategoryPlot plot = barChart.getCategoryPlot();
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setSeriesPaint(1, new Color(200,80,80));
    renderer.setSeriesPaint(0, new Color(80,200,80));
    renderer.setDrawBarOutline(false);
    renderer.setShadowVisible(false); // Remove shadows
    renderer.setBarPainter(new StandardBarPainter());
    ChartPanel chartPanel = new ChartPanel(barChart);
    chartPanel.setPreferredSize(new Dimension(800, 600));
    setContentPane(chartPanel);
  }

  private CategoryDataset createDataset(ITrie trie) {
    final String INSERT = "Insert";
    final String SEARCH = "Search";

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    List<String> words = generateRandomWords(NUM_WORDS, WORD_LENGTH);

    long insertStartTime = System.nanoTime();
    for (String word : words) {
      trie.insert(word);
    }
    long insertEndTime = System.nanoTime();
    long insertDuration = insertEndTime - insertStartTime;

    long searchStartTime = System.nanoTime();
    for (String word : words) {
      trie.search(word);
    }
    long searchEndTime = System.nanoTime();
    long searchDuration = searchEndTime - searchStartTime;

    dataset.addValue(insertDuration, INSERT, INSERT);
    dataset.addValue(searchDuration, SEARCH, SEARCH);

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
}