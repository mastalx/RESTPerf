package ch.tie.perf.scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Statistics {

  private final List<Measurement> measurements = Collections.synchronizedList(new ArrayList<>());

  public void updateStatistics(String name, long duration) {
    measurements.add(new Measurement(name, System.currentTimeMillis(), TimeUnit.NANOSECONDS.toMillis(duration)));
  }

  public List<Measurement> getMeasurements() {
    return measurements;
  }

}
