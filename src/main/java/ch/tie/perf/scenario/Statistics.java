package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Statistics extends ConcurrentHashMap<String, List<Pair<Long, Long>>> {

  private static final long serialVersionUID = 1L;
  private static final Logger LOGGER = LogManager.getLogger(AbstractScenario.class);

  public void updateStatistics(long durationInNanos, String key) {

    List<Pair<Long, Long>> map = get(key);
    if (map == null) {
      map = new ArrayList<>();
      put(key, map);
    }

    Pair<Long, Long> entry = new Pair<>(System.currentTimeMillis(), TimeUnit.NANOSECONDS.toMillis(durationInNanos));
    LOGGER.trace("updating Statistics:" + key + ", value: " + entry);
    map.add(entry);
  }
}
