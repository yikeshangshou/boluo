package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since May 24, 2014
 */
public class District extends BaseModel implements Serializable {
  private static final long serialVersionUID = 9129265106431765023L;

  private long districtId;
  private long parentId;
  private String nameEn;
  private String shortName;
  private int level;
  private int orderNumber;

  private District parent;

  public District() {
    super();
  }

  public int getLevel() {
    return level;
  }

  public District getParent() {
    return parent;
  }

  public long getParentId() {
    return parentId;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setParent(District parent) {
    this.parent = parent;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  public long getDistrictId() {
    return districtId;
  }

  public void setDistrictId(long districtId) {
    this.districtId = districtId;
  }

  public String getNameEn() {
    return nameEn;
  }

  public void setNameEn(String nameEn) {
    this.nameEn = nameEn;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public int getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(int orderNumber) {
    this.orderNumber = orderNumber;
  }

  public District getProvince() {
    District target = this;
    while (target != null && target.getLevel() > 1) {
      target = target.getParent();
    }

    if (target != null && target.getLevel() == 1) {
      return target;
    }

    return null;
  }

  public District getCity() {
    District target = this;
    while (target != null && target.getLevel() > 2) {
      target = target.getParent();
    }

    if (target != null && target.getLevel() == 2) {
      return target;
    }

    return null;
  }

  public District getDistrict() {
    District target = this;
    while (target != null && target.getLevel() > 3) {
      target = target.getParent();
    }

    if (target != null && target.getLevel() == 3) {
      return target;
    }

    return null;
  }

}
