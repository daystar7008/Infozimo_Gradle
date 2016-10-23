package com.infozimo.beans;

public class User {

	public static final String USER = "user";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String GENDER = "gender";
	public static final String DOB = "dob";
	public static final String PICTURE = "picture";
	public static final String PWD = "pwd";
	public static final String MOBILE = "mobile";
	public static final String ACTIVATED = "activated";
	
	private String userId;
	private String userName;
	private char gender;
	private String dob;
	private String picture;
	private String pwd;
	private String mobile;
	private String activated;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getActivated() {
		return activated;
	}

	public void setActivated(String activated) {
		this.activated = activated;
	}
}
