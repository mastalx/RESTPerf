package ch.tie.perf.scenario;


public class Measurement implements Comparable<Measurement> {


  private final String name;
  private final long timestamp;
  private final long duration;

  public Measurement(String name, long timestamp, long duration) {
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + (int) (duration ^ (duration >>> 32));
    result = (prime * result) + ((name == null) ? 0 : name.hashCode());
    result = (prime * result) + (int) (timestamp ^ (timestamp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Measurement other = (Measurement) obj;
    if (duration != other.duration) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (timestamp != other.timestamp) {
      return false;
    }
    return true;
  }
}
