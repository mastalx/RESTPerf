package ch.tie.perf.scenario;


public class FileHolder {

  private final String fileName;
  private final byte[] bytes;

  public FileHolder(String fileName, byte[] bytes) {
    this.fileName = fileName;
    this.bytes = bytes;
  }

  public String getFileName() {
    return fileName;
  }

  public byte[] getBytes() {
    return bytes;
  }
}
