package com.infozimo.android;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import com.infozimo.adapter.InfoAdapter;
import com.infozimo.adapter.InfoAdapter.DisplayType;
import com.infozimo.beans.Info;
import com.infozimo.ui.util.DownloadImageTask;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;
import com.infozimo.webservice.WebServiceURL;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class ProfileFragment extends Fragment {

	private SharedPreferences sharedPref;
	private String userId;
	private Context context;
	private View fragmentView;
	
	private RelativeLayout layoutCover;
	private ListView lvProfileInfo;
	private ImageView ivProfPic;
	private TextView tvUserName;
	private TextView tvEditProfile;

	private List<Info> infoList;
	private InfoAdapter infoAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
	
		fragmentView = inflater.inflate(R.layout.layout_profile, container, false);
		
		this.context = fragmentView.getContext();
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		layoutCover = (RelativeLayout) fragmentView.findViewById(R.id.layoutCover);
		lvProfileInfo = (ListView) fragmentView.findViewById(R.id.lvProfileInfo);
		ivProfPic = (ImageView) fragmentView.findViewById(R.id.ivProfPicProfile);
		tvUserName = (TextView) fragmentView.findViewById(R.id.tvUserName);
		tvEditProfile = (TextView) fragmentView.findViewById(R.id.tvEditProfile);

		userId = sharedPref.getString(Constants.USER_ID, "");
		tvUserName.setText(sharedPref.getString(Constants.USER_NAME, ""));
		new DownloadImageTask(ivProfPic).execute(WebServiceURL.USER_PIC.toString() + userId);

		tvEditProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent editIntent = new Intent(MyApplication.getAppContext(), LoginActivity.class);
				editIntent.putExtra("mode", "edit");
				startActivity(editIntent);
				getActivity().finish();
			}
		});

		populateInfo(0);
		
		return fragmentView;
	}
	
	private void populateInfo(int startRow){
		InfoRequestTask getRequest = new InfoRequestTask(WebServiceURL.GET_INFO_BY_USERID.toString() + userId + "/" + startRow);
		getRequest.execute();
	}
	
	public class InfoScrollListener implements AbsListView.OnScrollListener {

		private int preLast;
		
		@Override
		public void onScroll(AbsListView arg0, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
			final int lastItem = firstVisibleItem + visibleItemCount;
			if (lastItem == totalItemCount) {
				if (preLast != lastItem) { 
					preLast = lastItem;
					
					populateInfo(preLast);
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView arg0, int arg1) {
			
		}
		
	}

	public class InfoRequestTask extends AsyncTask<String, Void, String> {

		private HttpClient client;
		private HttpGet httpGet;
		private HttpResponse response;
		private HttpEntity entity;
		
		private String url;
		
		public InfoRequestTask(String url) {
			this.url = url;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
			
			client = new DefaultHttpClient();
			
			httpGet = new HttpGet(url);
			
			String result = null;
			try {
				response = client.execute(httpGet);
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
				if(infoList == null) {
					infoList = JSONParser.parseInfo(response);
					if(infoList != null){
						infoAdapter = new InfoAdapter(context, R.layout.view_info, infoList);
						infoAdapter.setDisplayType(DisplayType.PROFILE);
						lvProfileInfo.setAdapter(infoAdapter);
						lvProfileInfo.setOnScrollListener(new InfoScrollListener());
					}
				} else {
					List<Info> list = JSONParser.parseInfo(response);
					if(list != null && list.size() > 0){
						infoList.addAll(JSONParser.parseInfo(response));
						if(infoAdapter != null){
							infoAdapter.notifyDataSetChanged();
						}
					}
				}
				
			} catch (Exception e) {
				Log.e("ProfileFragment.Java", e.getMessage());
			}
		}

	}

	
}
