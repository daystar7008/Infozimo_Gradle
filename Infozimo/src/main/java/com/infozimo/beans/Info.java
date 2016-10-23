package com.infozimo.beans;

public class Info {

	public static final String INFO_ID = "info_id";
	public static final String USER_ID = "user_id";
	public static final String USER_NAME = "user_name";
	public static final String TAG_ID = "tag_id";
	public static final String INFO_DETAIL = "info_detail";
	public static final String INFO_PICTURE = "info_picture";
	public static final String PICTURE_BYTES = "picture_bytes";
	public static final String LIKE_COUNT = "like_count";
	public static final String LIKED = "liked";
	public static final String USER_PIC_URL = "user_pic_url";
	
	private int infoId;
	private String userId;
	private String userName;
	private int tagId;
	private String infoDetail;
	private String infoPicture;
	private byte[] pictureBytes;
	private byte[] profilePicBytes;
	private int likeCount;
	private char liked;
	private String userPicUrl;

	public int getInfoId() {
		return infoId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public void setUserName(String userName){
		this.userName = userName;
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public String getInfoDetail() {
		return infoDetail;
	}

	public void setInfoDetail(String infoDetail) {
		this.infoDetail = infoDetail;
	}

	public String getInfoPicture() {
		return infoPicture;
	}

	public void setInfoPicture(String infoPicture) {
		this.infoPicture = infoPicture;
	}

	public byte[] getPictureBytes() {
		return pictureBytes;
	}

	public void setPictureBytes(byte[] pictureBytes) {
		this.pictureBytes = pictureBytes;
	}

	public byte[] getProfilePicBytes() {
		return profilePicBytes;
	}

	public void setProfilePicBytes(byte[] profilePicBytes) {
		this.profilePicBytes = profilePicBytes;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	
	public char getLiked() {
		return liked;
	}
	
	public void setLiked(char liked){
		this.liked = liked;
	}

	public void setUserPicUrl(String userPicUrl) {
		this.userPicUrl = userPicUrl;
	}
	
	public String getUserPicUrl() {
		return userPicUrl;
	}

}
