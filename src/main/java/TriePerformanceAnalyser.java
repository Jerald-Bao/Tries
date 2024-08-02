import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
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

  public TriePerformanceAnalyser(String title) {
    super(title);
  }

  public void createCharts(ITrie... tries){
    final String INSERT = "Insertion";
    final String SEARCH = "Searching";
    final String DELETE = "Deletion";
    final String SPACE = "Memory Usage";

    int n = 40000;

    final DefaultCategoryDataset datasetTime = new DefaultCategoryDataset();
    JFreeChart barChartTime = ChartFactory.createBarChart(
        "Trie Operation Time, n = "+n ,
        "Trie variation",
        "Time (nanoseconds)",
        datasetTime,
        PlotOrientation.VERTICAL,
        true, true, false);

    final DefaultCategoryDataset datasetSpace = new DefaultCategoryDataset();
    JFreeChart barChartSpace = ChartFactory.createBarChart(
        "Trie Memory Usage, n = "+n,
        "Trie variation",
        "Memory Usage (Bytes)",
        datasetSpace,
        PlotOrientation.VERTICAL,
        true, true, false);

    CategoryPlot plot = barChartTime.getCategoryPlot();
    BarRenderer rendererTime = (BarRenderer) plot.getRenderer();
    plot = barChartSpace.getCategoryPlot();
    BarRenderer rendererSpace = (BarRenderer) plot.getRenderer();

    for (ITrie trie: tries) {

      PerformanceData data = createDataset(n, trie);
      datasetTime.addValue(data.avg_insertion_time, INSERT, trie.getTitle());
      rendererTime.setSeriesPaint(0, new Color(220,90,90));
      datasetTime.addValue(data.avg_searching_time, SEARCH, trie.getTitle());
      rendererTime.setSeriesPaint(1, new Color(90,220,90));
      datasetTime.addValue(data.avg_deletion_time, DELETE, trie.getTitle());
      rendererTime.setSeriesPaint(2, new Color(90,90,220));

      datasetSpace.addValue(data.memory_usage, SPACE, trie.getTitle());
      rendererSpace.setSeriesPaint(0, new Color(220,100,100));
    }

    rendererTime.setDrawBarOutline(false);
    rendererTime.setShadowVisible(false); // Remove shadows
    rendererTime.setBarPainter(new StandardBarPainter());
    rendererSpace.setItemMargin(0.00);
    rendererTime.setMaximumBarWidth(0.05);
    rendererTime.setItemMargin(0.0);

    rendererSpace.setDrawBarOutline(false);
    rendererSpace.setShadowVisible(false); // Remove shadows
    rendererSpace.setBarPainter(new StandardBarPainter());
    rendererSpace.setItemMargin(0.0);
    rendererSpace.setMaximumBarWidth(0.05);
    rendererSpace.setItemMargin(0.0);

    saveChartAsImage(barChartTime, n+"-Time.png");
    saveChartAsImage(barChartSpace, n+"-Space.png");

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

  private PerformanceData createDataset(int n, ITrie trie) {
    PerformanceData data = new PerformanceData();


    List<String> wordsInsert = wordListUtil.LoadFromFile("src/main/resources/wordlist.txt",n );
    List<String> wordsSearch = wordListUtil.LoadFromFile("src/main/resources/wordlist.txt",n);
    List<String> wordsDelete = new ArrayList<>(wordsInsert);
    Collections.shuffle(wordsDelete);
    wordsDelete.subList(0,n/10);
    try (PerformanceMonitor monitor = new PerformanceMonitor()) {
      for (String word : wordsInsert) {
        trie.insert(word);
      }
      data.avg_insertion_time = (float)monitor.getRunningTime()/n;
      data.memory_usage = monitor.getMemoryUsage();
      monitor.reset();
      for (String word : wordsSearch) {
        trie.search(word);
      }
      data.avg_searching_time = (float)monitor.getRunningTime()/n;
      monitor.reset();
      for (String word : wordsDelete) {
        trie.remove(word);
      }
      data.avg_deletion_time = (float)monitor.getRunningTime()/n;
    } catch (Exception e) {
      System.err.println("An exception is thrown during the performance testing");
      e.printStackTrace();
    }

    return data;
  }





  private void saveChartAsImage(JFreeChart chart, String dst){
    try {
      File imageFile = new File(dst);
      ChartUtils.saveChartAsPNG(imageFile, chart, 800, 600);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    TriePerformanceAnalyser chart = new TriePerformanceAnalyser("Trie Performance Analysis");
    chart.createCharts(new Trie(), new MockTrie(), new PatriciaTrie());

  }

  public class PerformanceMonitor implements AutoCloseable{
    long startTime = 0;
    long startMemoryUsage = 0;
    public PerformanceMonitor(){
      forceGarbageCollection();
      startTime = System.nanoTime();
      startMemoryUsage = getTotalMemoryUsage();
    }
    public void reset(){
      startTime = System.nanoTime();
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