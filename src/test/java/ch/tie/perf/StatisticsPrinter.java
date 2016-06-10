package ch.tie.perf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatisticsPrinter {

  private static final Logger LOGGER = LogManager.getLogger(StatisticsPrinter.class);


  public void printStatistics(String prefix, Map<String, Map<Long, Long>> statistics) throws IOException {

    for (Map.Entry<String, Map<Long, Long>> entry : statistics.entrySet()) {
      Map<Long, Long> stats = entry.getValue();
      Comparator<Long> longComparator = new Comparator<Long>() {

        @Override
        public int compare(Long o1, Long o2) {
          return Long.compare(o1, o2);
        }
      };
      TreeMap<Long, Long> sorted = new TreeMap<>(longComparator);
      sorted.putAll(stats);

      String categoryName = entry.getKey();
      LOGGER.info(categoryName + ": " + sorted);


      Stream<String> linesStream = sorted.entrySet().stream().map(e -> e.getKey() + ";" + e.getValue());

      List<String> lines = new ArrayList<String>();
      Iterator<String> it = linesStream.iterator();
      while (it.hasNext()) {
        lines.add(it.next());
      }
      Files.write(Paths.get(prefix + categoryName), lines, StandardOpenOption.CREATE);
    }
  }

}
