package com.infozimo.listeners;

import org.json.JSONException;

import com.infozimo.beans.Tag;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TagClickListener implements ListView.OnItemClickListener {
	
	private SharedPreferences sharedPref;
	private TransType transType;
	
	public TagClickListener(TagClickListener.TransType transType){
		this.transType = transType;
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(view.getContext());		
		
		Tag tag = (Tag) adapterView.getItemAtPosition(position);
		ServiceCaller serviceCaller = new ServiceCaller();
		
		String response = null;
		
		if(TransType.ADD.equals(transType)){
			response = serviceCaller.callAddUserTagService(sharedPref.getString("userId", ""), tag.getTagId());
		} else if(TransType.REMOVE.equals(transType)){
			response = serviceCaller.callRemoveUserTagService(sharedPref.getString("userId", ""), tag.getTagId());
		}
		
		boolean isSucceeded = false;
		try {
			isSucceeded = JSONParser.isSucceeded(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(isSucceeded){
			@SuppressWarnings("unchecked")
			ArrayAdapter<Tag> adapter = (ArrayAdapter<Tag>) adapterView.getAdapter();
			adapter.remove(tag);
			adapter.notifyDataSetChanged();
			
			String message = null;
			if(TransType.ADD.equals(transType)){
				message = "Added to Your Tags";
			} else if(TransType.REMOVE.equals(transType)){
				message = "Removed From Your Tags";
			}
			Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public enum TransType {
		ADD, REMOVE;
	}

}
