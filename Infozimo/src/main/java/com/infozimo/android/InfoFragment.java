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
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;
import com.infozimo.webservice.WebServiceURL;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

@SuppressWarnings("deprecation")
public class InfoFragment extends Fragment {

	private SharedPreferences sharedPref;
	
	private ListView lvInfoList;
	private Context context;
	private View fragmentView;
	private List<Info> infoList;
	private InfoAdapter infoAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		fragmentView = inflater.inflate(R.layout.layout_info, container, false);
		
		this.context = fragmentView.getContext();
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		populateInfo(0);
		
		return fragmentView;
	}

	private void populateInfo(int startRow) {
		
		callUserTagInfoService(sharedPref.getString(Constants.USER_ID, ""), startRow);
	}
	
	public void callUserTagInfoService(String userId, int startRow){
		InfoRequestTask getRequest = new InfoRequestTask(WebServiceURL.GET_INFO_BY_USER_TAG.toString() + userId + "/" + startRow);
		
		try {
			getRequest.execute();
			
		} catch (Exception e) {
			Log.e("ServiceCaller", e.getMessage());
		}
	}
	
	private void openFirstLaunchDialog(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle("Have You Selected Any Tags?");
		dialog.setMessage("We'll Help You Out To Setup");
		
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Fragment fragment = new TagsFragment();
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			}
		});
		
		dialog.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		dialog.show();
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
					ServiceCaller pointsService = new ServiceCaller();
					try {
						pointsService.callUpdatePointsService(sharedPref.getString(Constants.USER_ID, ""), "viewer");
					} catch (JSONException e) {
						e.printStackTrace();
					}
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
						lvInfoList = (ListView) fragmentView.findViewById(R.id.lvInfo);
						infoAdapter = new InfoAdapter(context, R.layout.view_info, infoList);
						infoAdapter.setDisplayType(DisplayType.HOME);
						lvInfoList.setAdapter(infoAdapter);
						lvInfoList.setOnScrollListener(new InfoScrollListener());
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
				
				if(infoList != null && infoList.size() < 1){
					openFirstLaunchDialog();
					return;
				}
			} catch (Exception e) {
				Log.e("InfoFragment.Java", e.getMessage());
			}
			
		}

	}
	
}
