package ch.tie.perf.scenario;


public class Measurement implements Comparable<Measurement> {


  private final String name;
  private final long id;
  private final long timestamp;
  private final long duration;

  public Measurement(String name, long id, long timestamp, long duration) {
    this.id = id;
    this.name = name;
    this.timestamp = timestamp;
    this.duration = duration;
  }

  public String getName() {
    return name;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getDuration() {
    return duration;
  }

  @Override
  public int compareTo(Measurement o) {
    int retVal = this.name.compareTo(o.name);
    if (retVal != 0) {
      return retVal;
    }
    retVal = Long.compare(this.timestamp, o.timestamp);
    if (retVal != 0) {
      return retVal;
    }
    return Long.compare(this.duration, o.duration);
  }

  public long getId() {
    return id;
  }
}
