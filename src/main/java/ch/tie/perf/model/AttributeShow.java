package ch.tie.perf.model;


public class AttributeShow {

  private String datatype;
  private boolean readonly;
  private boolean mandatory;
  private String attributeName;

  public AttributeShow() {
  }

  @Override
  public String toString() {
    return "AttributeShow [datatype=" + datatype + ", readonly=" + readonly + ", mandatory=" + mandatory
        + ", attributeName=" + attributeName + "]";
  }

  public AttributeShow(String datatype, boolean readonly, boolean mandatory, String attributeName) {
    this.datatype = datatype;
    this.readonly = readonly;
    this.mandatory = mandatory;
    this.attributeName = attributeName;
  }


  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public boolean isReadonly() {
    return readonly;
  }

  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }


  public String getAttributeName() {
    return attributeName;
  }


  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

  public enum type {
    BODY_ALL("BODY%"), BODY0("BODY0"), BODY1("BODY1"), BODY2("BODY2"), BODY3("BODY3"), BUTTON("BUTTON"), BUTTON0(
        "BUTTON0"), BUTTON1("BUTTON1"), BUTTON2("BUTTON2"), BUTTON3("BUTTON3"), BUTTON_NEXT("BUTTON_NEXT"), BUTTON_SELECT(
        "BUTTON_SELECT"), CHOOSE("CHOOSE"), CHOSEN("CHOSEN"), CONFIRMATION("CONFIRMATION"), DETAIL("DETAIL"), DETAILVIEW(
        "DETAILVIEW"), HDR_FILE("HDR-FILE"), HEADER("HEADER"), HIDDEN("HIDDEN"), IMPORTER_CONFIG("IMPORTER_CONFIG"), KONTIERUNGSPOSITION(
        "KONTIERUNGSPOSITION"), MBODY("MBODY"), METADATA("METADATA"), TAB("TAB"), XML("XML");

    private final String key;

    private type(final String key) {
      this.key = key;
    }

    public String getKey() {
      return key;
    }

  }
}
