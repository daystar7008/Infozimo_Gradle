package com.infozimo.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import com.infozimo.beans.Info;
import com.infozimo.beans.Tag;
import com.infozimo.ui.util.DownloadImageTask;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;
import com.infozimo.webservice.WebServiceURL;
import com.startapp.android.publish.StartAppAd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class PostFragment extends Fragment {

	private ImageView ivUserPic;
	private TextView tvUser;
	private EditText etInfoDetail;
	private TextView tvSelectedTag;
	private ImageView ivImagePost;
	private GridView gvTags;
	private Button btnChoosePic;
	private Button btnPost;
	
	private byte[] imageInByte;
	
	private int imgPickResult = 1;
	
	private SharedPreferences sharedPref;
	
	private String userId;
	private Tag selectedTag;
	
	private Context context;
	private View fragmentView;
	
	private Bundle bundle;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		fragmentView = inflater.inflate(R.layout.layout_post, container, false);

		this.context = fragmentView.getContext();
		this.bundle = savedInstanceState;
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		ivUserPic = (ImageView) fragmentView.findViewById(R.id.ivUserPicPost);
		tvUser  = (TextView) fragmentView.findViewById(R.id.tvUserPost);
		etInfoDetail = (EditText) fragmentView.findViewById(R.id.etInfoDetailPost);
		tvSelectedTag = (TextView) fragmentView.findViewById(R.id.tvSelectedTag);
		ivImagePost = (ImageView) fragmentView.findViewById(R.id.ivImagePost);
		
		userId = sharedPref.getString(Constants.USER_ID, "");
		
		tvUser.setText(sharedPref.getString(Constants.USER_NAME, ""));

		new DownloadImageTask(ivUserPic).execute(WebServiceURL.USER_PIC.toString() + userId);
		
		populateTaggedTags();
		
		btnChoosePic = (Button) fragmentView.findViewById(R.id.btnChoosePic);
		btnChoosePic.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/jpeg");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Choose Picture"), imgPickResult);
			}
		});
		
		btnPost = (Button) fragmentView.findViewById(R.id.btnPost);
		btnPost.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(selectedTag == null){
					Toast.makeText(context, "Select Tag", Toast.LENGTH_SHORT).show();
					return;
				} 
				
				if(imageInByte == null) {
					Toast.makeText(context, "Select Image", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Info info = new Info();
				info.setUserId(userId);
				info.setInfoDetail(etInfoDetail.getText().toString());
				info.setTagId(selectedTag.getTagId());
				info.setInfoPicture(Base64.encodeToString(imageInByte, Base64.DEFAULT));
				
				try {
					callAddInfoService(info);
				} catch (JSONException e) {
					Toast.makeText(context, "Failed To Get Response From Server", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		return fragmentView;
	}
	
	public void callAddInfoService(Info info) throws JSONException {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.USER_ID, info.getUserId());
		values.put(Info.TAG_ID, info.getTagId());
		values.put(Info.INFO_DETAIL, info.getInfoDetail());
		values.put(Info.INFO_PICTURE, info.getInfoPicture());
		
		InfoPostRequestTask postRequest = new InfoPostRequestTask(WebServiceURL.ADD_INFO.toString(), JSONParser.jsonOf(info));
		postRequest.execute();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	 
	    if (requestCode == imgPickResult && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
	 
	        Uri uri = data.getData();
	        
	        try {
	            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
	            
	            ByteArrayOutputStream stream = new ByteArrayOutputStream();   
	            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, stream);
	            imageInByte = stream.toByteArray(); 
	            
	            //Toast.makeText(this, String.valueOf(lengthbmp), Toast.LENGTH_LONG).show();
	            ivImagePost.setImageBitmap(bitmap);
	            ivImagePost.setVisibility(ImageView.VISIBLE);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	private void populateTaggedTags(){
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callUserTagService(sharedPref.getString(Constants.USER_ID, ""));
		
		List<Tag> tags =  null;
		try {
			tags = JSONParser.parseTags(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(tags != null){
			gvTags = (GridView) fragmentView.findViewById(R.id.gvTagsPost);
			ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(context, R.layout.tag_item, tags);
			gvTags.setAdapter(adapter);
			gvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> list, View arg1, int position, long arg3) {
					selectedTag = (Tag) list.getItemAtPosition(position);
					tvSelectedTag.setText(selectedTag.getTagName());
					tvSelectedTag.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);
					tvSelectedTag.setVisibility(TextView.VISIBLE);
					
					tvSelectedTag.setOnClickListener(new TextView.OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							tvSelectedTag.setVisibility(TextView.INVISIBLE);
							selectedTag = null;
						}
					});
				}
			});
			
			adapter.notifyDataSetChanged();
		}
	}
	
	public class InfoPostRequestTask extends AsyncTask<String, Void, String> {

		private HttpClient client;
		private HttpPost post;
		
		private AlertDialog progressDialog;
		
		private String url;
		private String jsonParam;
		
		public InfoPostRequestTask(String url, String jsonParam) {
			this.url = url;
			this.jsonParam = jsonParam;
			
			progressDialog = new AlertDialog.Builder(context).create();
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(progressDialog != null){
				progressDialog.setView(new ProgressBar(context));
				progressDialog.setCancelable(false);
				progressDialog.show();
			}
		}
		
		@Override
		protected String doInBackground(String... args) {
			client = new DefaultHttpClient();
			post = new HttpPost(url);
			
			if(jsonParam != null){
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("json", jsonParam));
				UrlEncodedFormEntity formEntity = null;
				try {
					formEntity = new UrlEncodedFormEntity(params);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				if(formEntity != null){
					post.setEntity(formEntity);
				}
			}
			
			HttpResponse response = null;
			HttpEntity entity = null;
			
			String result = null;
			try {
				response = client.execute(post);
				entity = response.getEntity();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try {
				result = EntityUtils.toString(entity);
				Log.d("HttpUtil", result);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			
			try {
				if(JSONParser.isSucceeded(response)){
					Toast.makeText(context, "Post Successful", Toast.LENGTH_SHORT).show();
					
					ServiceCaller pointsService = new ServiceCaller();
					try {
						pointsService.callUpdatePointsService(sharedPref.getString(Constants.USER_ID, ""), "poster");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					StartAppAd.showSplash(getActivity(), bundle);
					
					Fragment fragment = new InfoFragment();
					FragmentManager fragmentManager = getFragmentManager();
					fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			if(progressDialog != null) {
				progressDialog.dismiss();
			}
		}
		
	}
	
}
