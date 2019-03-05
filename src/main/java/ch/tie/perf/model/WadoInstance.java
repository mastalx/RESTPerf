package ch.tie.perf.model;


import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.jaxrs.JaxRsLinkBuilder;


public class WadoInstance  {

  private String studyInstanceUid;
  private String seriesInstanceUid;
  private String sopInstanceUid;

  public WadoInstance(String studyInstanceUid, String seriesInstanceUid, String sopInstanceUid) {
    this.studyInstanceUid = studyInstanceUid;
    this.seriesInstanceUid = seriesInstanceUid;
    this.sopInstanceUid = sopInstanceUid;
  }

  public String getStudyInstanceUid() {
    return studyInstanceUid;
  }

  public void setStudyInstanceUid(String studyInstanceUid) {
    this.studyInstanceUid = studyInstanceUid;
  }

  public String getSeriesInstanceUid() {
    return seriesInstanceUid;
  }

  public void setSeriesInstanceUid(String seriesInstanceUid) {
    this.seriesInstanceUid = seriesInstanceUid;
  }

  public String getSopInstanceUid() {
    return sopInstanceUid;
  }

  public void setSopInstanceUid(String sopInstanceUid) {
    this.sopInstanceUid = sopInstanceUid;
  }


}
