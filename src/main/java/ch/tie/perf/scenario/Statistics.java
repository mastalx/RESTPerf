package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Statistics {

  private final List<Measurement> measurements = Collections.synchronizedList(new ArrayList<>());

  public void updateStatistics(String name, long requestId, long duration) {
    Measurement measurement = new Measurement(name, requestId, System.currentTimeMillis(),
        TimeUnit.NANOSECONDS.toMillis(duration));
    measurements.add(measurement);
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }
}
