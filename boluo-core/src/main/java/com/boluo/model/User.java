package com.boluo.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Mar 10, 2014
 */
public class User extends BaseModel implements Serializable {
  private static final long serialVersionUID = -8028956919647703012L;

  private String avatar;
  private int bindStatus;
  private String birthday;
  private String city;
  private String email;
  private String firstName;
  private String gender;
  private String lastName;
  private String locale;
  private String password;
  private String phone;
  private String profile;
  private String roles;
  private String securityCode;
  private String username;

  public String getAvatar() {
    return avatar;
  }

  public int getBindStatus() {
    return bindStatus;
  }

  public String getBirthday() {
    return birthday;
  }

  public String getCity() {
    return city;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getGender() {
    return gender;
  }

  public String getLastName() {
    return lastName;
  }

  public String getLocale() {
    return locale;
  }

  public String getPassword() {
    return password;
  }

  public String getPhone() {
    return phone;
  }

  public String getProfile() {
    return profile;
  }

  public String getRoles() {
    return roles;
  }

  public String getSecurityCode() {
    return securityCode;
  }

  public String getUsername() {
    return username;
  }

  public boolean isCompany() {
    return StringUtils.contains(roles, "company");
  }

  public boolean isWorker() {
    return StringUtils.contains(roles, "worker");
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public void setBindStatus(int bindStatus) {
    this.bindStatus = bindStatus;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  public void setSecurityCode(String securityCode) {
    this.securityCode = securityCode;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public String toString() {
    return "User [email=" + email + ", username=" + username + ", id=" + id + "]";
  }

}
