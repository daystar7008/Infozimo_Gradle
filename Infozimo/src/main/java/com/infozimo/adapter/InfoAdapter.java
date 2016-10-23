package com.infozimo.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.infozimo.android.R;
import com.infozimo.beans.Info;
import com.infozimo.ui.util.DownloadImageTask;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;
import com.infozimo.webservice.WebServiceURL;
import com.startapp.android.publish.StartAppAd;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class InfoAdapter extends ArrayAdapter<Info> {

	private SharedPreferences sharedPref;
	
	private Context mContext = null;
    private int id;
    
    private LikeClickListener likeListener;
    
    private LayoutInflater inflater;
    
    private Drawable drawableUnlike;
    private Drawable drawableLike;
	
    private DisplayType displayType = DisplayType.HOME;
    
    private StartAppAd startAppAd;
    
	public InfoAdapter(Context context, int resource, List<Info> infoList) {
		super(context, resource, infoList);
		mContext = context;
        id = resource;
        
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        
        drawableUnlike = mContext.getResources().getDrawable(R.drawable.ic_liked);
        drawableLike = mContext.getResources().getDrawable(R.drawable.ic_like);
        
        startAppAd = new StartAppAd(mContext);
	}
	
	public void setDisplayType(DisplayType displayType) {
		this.displayType = displayType;
	}
	
	@Override
    public View getView(int position, View v, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();
		
        View mView = v;
        
        if(position == 7 || position == 35) {
        	startAppAd.showAd();
			startAppAd.loadAd();
        }
        
        if(mView == null){
            mView = inflater.inflate(id, null);
            viewHolder.tvUser = (TextView) mView.findViewById(R.id.tvUser);
            viewHolder.ivUserPic = (ImageView) mView.findViewById(R.id.ivUserPic);
            viewHolder.tvInfoDetail = (TextView) mView.findViewById(R.id.tvInfoDetail);
            viewHolder.layoutImageInfo = (LinearLayout) mView.findViewById(R.id.layoutImageInfo);
            viewHolder.ivInfoImage = (ImageView) mView.findViewById(R.id.ivInfoImage);
            viewHolder.ibShareInfo = (Button) mView.findViewById(R.id.ibShareInfo);
            viewHolder.ibLikeInfo = (Button) mView.findViewById(R.id.ibLikeInfo);
            viewHolder.ivDeletePost = (ImageView) mView.findViewById(R.id.ivDeletePost);
            mView.setTag(viewHolder);
        } else {
        	viewHolder = (ViewHolder) mView.getTag();
        }
        Info info = getItem(position);
        
        viewHolder.tvUser.setText(info.getUserName());

		if(info.getUserPicUrl() != null && info.getUserPicUrl().contains("graph.facebook.com")) {
			new DownloadImageTask(viewHolder.ivUserPic).execute(info.getUserPicUrl());
		} else {
			new DownloadImageTask(viewHolder.ivUserPic).execute(WebServiceURL.USER_PIC.toString() + info.getUserId());
		}

        viewHolder.tvInfoDetail.setText(info.getInfoDetail());
        
        byte[] imageBytes = info.getPictureBytes();
        if(imageBytes != null){
        	final Bitmap bmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        	viewHolder.ivInfoImage.setImageBitmap(bmp);
        	
        	viewHolder.layoutImageInfo.setVisibility(LinearLayout.VISIBLE);
        	viewHolder.ivInfoImage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
					ImageView iv = new ImageView(mContext);
					iv.setImageBitmap(bmp);
					dialog.setView(iv);
					dialog.show();
				}
			});
        }
        
        viewHolder.values.put(Constants.INFO, info);
        viewHolder.values.put(Constants.USER_ID, sharedPref.getString(Constants.USER_ID, ""));
    	viewHolder.values.put("position", position);
    	
    	int likes = info.getLikeCount();
    	if(likes > 0){
    		viewHolder.ibLikeInfo.setText(likes + " Likes");
    	} else {
    		viewHolder.ibLikeInfo.setText("Like");
    	}
    	
        if(info.getLiked() != '\u0000' && info.getLiked() == 'Y'){
        	viewHolder.ibLikeInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawableUnlike, null, null);
        	viewHolder.values.put(Constants.LIKE_STATUS, Action.UNLIKE);
        } else {
        	viewHolder.ibLikeInfo.setCompoundDrawablesWithIntrinsicBounds(null, drawableLike, null, null);
        	viewHolder.values.put(Constants.LIKE_STATUS, Action.LIKE);
        }
        viewHolder.ibLikeInfo.setTag(viewHolder.values);
        likeListener = new LikeClickListener();
        viewHolder.ibLikeInfo.setOnClickListener(likeListener);
        
        viewHolder.ibShareInfo.setTag(viewHolder.values);
        viewHolder.ibShareInfo.setOnClickListener(new ShareClickListener());
        
        if(DisplayType.PROFILE.equals(displayType)){
        	viewHolder.ivDeletePost.setVisibility(View.VISIBLE);
        	viewHolder.ivDeletePost.setTag(getItem(position));
            viewHolder.ivDeletePost.setOnClickListener(new DeleteClickListener());
        }
        
        return mView;
	}
	
	private static class ViewHolder {
		TextView tvUser;
		ImageView ivUserPic;
		TextView tvInfoDetail;
		LinearLayout layoutImageInfo;
		ImageView ivInfoImage;
		Button ibShareInfo;
		Button ibLikeInfo;
		ImageView ivDeletePost;
		
		HashMap<String, Object> values = new HashMap<String, Object>();
	}
	
	private class LikeClickListener implements OnClickListener {

		private int infoId;
		private String userId;
		private Action action;
		private Info info;
		
		private HashMap<String, Object> values;
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			values = (HashMap<String, Object>) v.getTag();
			action = (Action) values.get(Constants.LIKE_STATUS);
			userId = (String) values.get(Constants.USER_ID);
			info = (Info) values.get(Constants.INFO);
			infoId = info.getInfoId();
			
			try {
				callLikeService(infoId, userId, action, info, v);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	private class ShareClickListener implements OnClickListener {

		private Info info;
		
		private HashMap<String, Object> values;
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			values = (HashMap<String, Object>) v.getTag();
			info = (Info) values.get(Constants.INFO);
			
			String filePath = createTempPicture(info.getPictureBytes());
			
			if(filePath != null){
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setPackage("com.whatsapp");
				intent.setType("image/jpeg");
				intent.putExtra(Intent.EXTRA_TEXT, info.getInfoDetail());
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filePath));
				mContext.startActivity(Intent.createChooser(intent, "Select"));
			} else {
				Toast.makeText(mContext, "Couldn't Share Info", Toast.LENGTH_LONG).show();
			}
			
		}
		
	}
	
	private class DeleteClickListener implements Button.OnClickListener {
		@Override
		public void onClick(View iv) {
			final Info info = (Info) iv.getTag();
			
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle("Delete Info?");
			dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					ServiceCaller service = new ServiceCaller();
					
					try {
						String response = service.callRemoveInfoService(info.getInfoId());
						if(JSONParser.isSucceeded(response)){
							remove(info);
							notifyDataSetChanged();
							Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(mContext, "Couldn't Delete. Try After Some Time", Toast.LENGTH_SHORT).show();
					}
					
				}
			});
			
			dialog.setNegativeButton("No", null);
			dialog.show();
			
		}
	}

	public enum Action {
		LIKE, UNLIKE;
	}
	
	public enum DisplayType {
		PROFILE, HOME;
	}
	
	private String createTempPicture(byte[] imageBytes){
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp" +".jpg");
		try {
		    file.createNewFile();
		    FileOutputStream fileStream = new FileOutputStream(file);
		    fileStream.write(imageBytes);
		    fileStream.close();
		    
		    return file.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void callLikeService(int infoId, String userId, Action action, Info info, View v) throws JSONException{
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put(Info.INFO_ID, infoId);
		values.put("user_id", userId);
		
		LikeRequestTask likeRequest = null;
		if(Action.LIKE.equals(action)){
			likeRequest = new LikeRequestTask(WebServiceURL.ADD_LIKE.toString(), JSONParser.jsonOf(values), action, info, v);
		} else if(Action.UNLIKE.equals(action)){
			likeRequest = new LikeRequestTask(WebServiceURL.REMOVE_LIKE.toString(), JSONParser.jsonOf(values), action, info, v);
		}
		
		likeRequest.execute();
	}
	
	public class LikeRequestTask extends AsyncTask<String, Void, String> {

		private HttpClient client;
		private HttpPost post;
		
		private String url;
		private String jsonParam;
		private Action action;
		private Info info;
		private View v;
		
		public LikeRequestTask(String url, String jsonParam, Action action, Info info, View v) {
			this.url = url;
			this.jsonParam = jsonParam;
			this.action = action;
			this.info = info;
			this.v = v;
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
				if(response != null && JSONParser.isSucceeded(response)){
					if(Action.LIKE.equals(action)){
			        	info.setLiked('Y');
			        	info.setLikeCount(info.getLikeCount() + 1);
			        	((Button) v).setText(info.getLikeCount() + " Likes");
			        	notifyDataSetChanged();
					} else if(Action.UNLIKE.equals(action)){
						info.setLiked('N');
						int likes = info.getLikeCount();
						info.setLikeCount(likes - 1);
			        	if(likes > 1){
			        		((Button) v).setText(info.getLikeCount() + " Likes");
			        	} else {
			        		((Button) v).setText("Like");
			        	}
			        	
			        	notifyDataSetChanged();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

}
