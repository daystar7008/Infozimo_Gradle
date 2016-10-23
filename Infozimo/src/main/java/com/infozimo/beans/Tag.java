package com.infozimo.beans;

public class Tag {
	
	public static final String TAG_ID = "tag_id";
	public static final String TAG_NAME = "tag_name";
	public static final String TAG_DESC = "tag_desc";

	private int tagId;
	private String tagName;
	private String tagDesc;

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagDesc() {
		return tagDesc;
	}

	public void setTagDesc(String tagDesc) {
		this.tagDesc = tagDesc;
	}
	
	@Override
	public String toString() {
		return this.tagName;
	}

}
