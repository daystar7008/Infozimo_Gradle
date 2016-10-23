package com.infozimo.util;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;

import com.infozimo.android.MyApplication;
import com.infozimo.beans.Info;
import com.infozimo.beans.Tag;
import com.infozimo.beans.User;
import com.infozimo.webservice.HttpGetRequestTask;
import com.infozimo.webservice.HttpPostRequestTask;
import com.infozimo.webservice.WebServiceURL;

public class ServiceCaller {
	
	private HttpGetRequestTask getRequest;
	private HttpPostRequestTask postRequest;
	
	public String callTagService(String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_TAGS.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
		
	}

	public String callTagByNameService(String tagName, String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_TAGS_BY_NAME.toString() + tagName + "/" + userId);
		Log.d("URL", WebServiceURL.GET_TAGS_BY_NAME.toString() + tagName + "/" + userId);
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUserTagService(String userId){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_USER_TAGS.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUserIdInfoService(String userId, int startRow){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_INFO_BY_USERID.toString() + userId + "/" + startRow);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callTagIdInfoService(String tagId, String userId, int startRow){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_INFO_BY_TAGID.toString() + tagId + "/" + userId + "/" + startRow);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUserTagInfoService(String userId, int startRow, ProgressDialog progressDialog){
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_INFO_BY_USER_TAG.toString() + userId + "/" + startRow, progressDialog);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}

	public String callAuthenticateService(String userId, String pwd) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(User.USER_ID, userId);
		values.put(User.PWD, pwd);

		try {
			postRequest = new HttpPostRequestTask(WebServiceURL.AUTHENTICATE.toString(), JSONParser.jsonOf(values));
		} catch(JSONException ex) {
			ex.printStackTrace();
		}
		try {
			String json = postRequest.execute().get();

			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callAddUserTagService(String userId, int tagId){
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Tag.TAG_ID, tagId);
		values.put("user_id", userId);
		
		try {
			postRequest = new HttpPostRequestTask(WebServiceURL.ADD_USER_TAG.toString(), JSONParser.jsonOf(values));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRemoveUserTagService(String userId, int tagId){
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Tag.TAG_ID, tagId);
		values.put("user_id", userId);
		
		try {
			postRequest = new HttpPostRequestTask(WebServiceURL.REMOVE_USER_TAG.toString(), JSONParser.jsonOf(values));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callAddInfoService(Info info, ProgressDialog progressDialog) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.USER_ID, info.getUserId());
		values.put(Info.TAG_ID, info.getTagId());
		values.put(Info.INFO_DETAIL, info.getInfoDetail());
		values.put(Info.INFO_PICTURE, info.getInfoPicture());
		
		postRequest = new HttpPostRequestTask(WebServiceURL.ADD_INFO.toString(), JSONParser.jsonOf(info), progressDialog);
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRemoveInfoService(int infoId) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Constants.INFO_ID_JSON, infoId);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.REMOVE_INFO.toString(), JSONParser.jsonOf(values));
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callAddLikeService(int infoId, String userId) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.INFO_ID, infoId);
		values.put("user_id", userId);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.ADD_LIKE.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRemoveLikeService(int infoId, String userId) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.INFO_ID, infoId);
		values.put("user_id", userId);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.REMOVE_LIKE.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callGetUserService(String userId) throws JSONException{
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_USER.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			Log.e("ServiceCaller", json);
			Log.e("ServiceCaller", WebServiceURL.GET_USER.toString() + userId);
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUpdateUserService(User user) throws JSONException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(User.USER_ID, user.getUserId());
		values.put(User.USER_NAME, user.getUserName());
		values.put(User.GENDER, user.getGender());
		values.put(User.DOB, user.getDob());
		values.put(User.PICTURE, user.getPicture());
		values.put(User.PWD, user.getPwd());
		values.put(User.MOBILE, user.getMobile());
		
		postRequest = new HttpPostRequestTask(WebServiceURL.UPDATE_USER.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callGetPointsService(String userId) throws JSONException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(User.USER_ID, userId);
		
		getRequest = new HttpGetRequestTask(WebServiceURL.GET_POINTS.toString() + userId);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}

	public String callSendMailService(String userId) throws JSONException {
		getRequest = new HttpGetRequestTask(WebServiceURL.SEND_MAIL.toString() + userId);

		try {
			String json = getRequest.execute().get();

			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callUpdatePointsService(String userId, String pointCategory) throws JSONException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(User.USER_ID, userId);
		values.put("point_category", pointCategory);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.UPDATE_POINTS.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callRedeemPointsService(String userId, String contact) throws JSONException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(User.USER_ID, userId);
		values.put("new_contact", contact);
		
		postRequest = new HttpPostRequestTask(WebServiceURL.REDEEM_POINTS.toString(), JSONParser.jsonOf(values));
		
		try {
			String json = postRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}

	public String callResetPasswordService(String userId) {
		getRequest = new HttpGetRequestTask(WebServiceURL.RESET_PWD.toString() + userId);

		try {
			String json = getRequest.execute().get();

			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}
	
	public String callFBCoverService(String userId, String accessToken){
		getRequest = new HttpGetRequestTask(Constants.FB_GRAPH_URL+ "/" + userId + "?fields=cover&access_token=" + accessToken);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
		
	}
	
	public String callFBProfileService(String userId, String accessToken) {
		getRequest = new HttpGetRequestTask(Constants.FB_GRAPH_URL+ "/" + userId + "?fields=gender,birthday&access_token=" + accessToken);
		
		try {
			String json = getRequest.execute().get();
			
			return json;
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
			return "Error";
		}
	}

}
