package ch.tie.perf.model;


import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.jaxrs.JaxRsLinkBuilder;


public class Obj extends ResourceSupport {

  @Override
  public String toString() {
    return "Obj [objId=" + objId + ", links=" + getLinks() + ", attributes=" + attributes + ", attributeShows="
        + attributeShows + ", objList=" + objList + ", uppers=" + uppers + ", lowers=" + lowers + "]";
  }

  private long objId;

  private Map<String, Object> attributes = new LinkedHashMap<>();
  private Map<String, AttributeShow> attributeShows = new LinkedHashMap<>();
  private Map<String, Obj> objList = new LinkedHashMap<>();
  private Map<String, Obj> uppers = new LinkedHashMap<>();
  private Map<String, Obj> lowers = new LinkedHashMap<>();

  public Obj() {
  }

  public Obj(long objId) {
    this.objId = objId;
  }


  public long getObjId() {
    return objId;
  }

  public void putLink(String rel, Class<?> clazz, Object... params) {
    add(JaxRsLinkBuilder.linkTo(clazz, params).withRel(rel));
  }

  public void putLinkWithSlash(String rel, String slash, Class<?> clazz, Object... params) {
    add(JaxRsLinkBuilder.linkTo(clazz, params).slash(slash).withRel(rel));
  }


  public Map<String, Object> getAttributes() {
    return attributes;
  }

  public void putAttribute(String name, Object value) {
    attributes.put(name, value);
  }

  public Map<String, AttributeShow> getAttributeShows() {
    return attributeShows;
  }

  public void putAttributeShow(String name, AttributeShow attributeShow) {

    attributeShows.put(name, attributeShow);
  }

  public Map<String, Obj> getObjList() {
    return objList;
  }

  public void putObjListItem(String name, Obj obj) {
    objList.put(name, obj);
  }

  public Map<String, Obj> getUppers() {
    return uppers;
  }

  public void putUpper(String name, Obj node) {

    uppers.put(name, node);
  }

  public Map<String, Obj> getLowers() {
    return lowers;
  }

  public void putLower(String name, Obj node) {
    lowers.put(name, node);
  }

  public void setObjId(long objId) {
    this.objId = objId;
  }


  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }


  public void setAttributeShows(Map<String, AttributeShow> attributeShows) {
    this.attributeShows = attributeShows;
  }


  public void setObjList(Map<String, Obj> objList) {
    this.objList = objList;
  }


  public void setUppers(Map<String, Obj> uppers) {
    this.uppers = uppers;
  }


  public void setLowers(Map<String, Obj> lowers) {
    this.lowers = lowers;
  }

}
