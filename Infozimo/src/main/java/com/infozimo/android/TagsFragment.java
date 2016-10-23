package com.infozimo.android;

import java.util.List;

import org.json.JSONException;

import com.infozimo.adapter.TagAdapter;
import com.infozimo.adapter.TagAdapter.SelectType;
import com.infozimo.beans.Tag;
import com.infozimo.listeners.TagClickListener;
import com.infozimo.listeners.TagClickListener.TransType;
import com.infozimo.util.Constants;
import com.infozimo.util.JSONParser;
import com.infozimo.util.ServiceCaller;

import android.text.Editable;
import android.text.TextWatcher;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

public class TagsFragment extends Fragment {

	private SharedPreferences sharedPref;
	
	private GridView gridViewTags;
	private Button btnTags, btnMyTags;
	private EditText etTagSearch;
	
	private Context context;
	private View fragmentView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		fragmentView = inflater.inflate(R.layout.layout_tags, container, false);
		
		this.context = fragmentView.getContext();
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		TagToggleListener toggleListener = new TagToggleListener();
		
		btnTags = (Button) fragmentView.findViewById(R.id.btnTags);
		btnTags.setOnClickListener(toggleListener);
		
		btnMyTags = (Button) fragmentView.findViewById(R.id.btnMyTags);
		btnMyTags.setOnClickListener(toggleListener);
		
		TagSearchListener searchListener = new TagSearchListener();
		
		etTagSearch = (EditText) fragmentView.findViewById(R.id.etTagSearch);
		etTagSearch.addTextChangedListener(searchListener);
		
		populateUnTaggedTags();
		
		return fragmentView;
	}

	private void populateUnTaggedTags(){
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callTagService(sharedPref.getString(Constants.USER_ID, ""));
		
		List<Tag> tags =  null;
		try {
			tags = JSONParser.parseTags(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(tags != null){
			gridViewTags = (GridView) fragmentView.findViewById(R.id.gridViewTags);
			TagAdapter adapter = new TagAdapter(context, R.layout.tag_item, tags, SelectType.CHECK);
			gridViewTags.setAdapter(adapter);
			etTagSearch.setVisibility(View.VISIBLE);
			gridViewTags.setOnItemClickListener(new TagClickListener(TransType.ADD));
			adapter.notifyDataSetChanged();
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
			gridViewTags = (GridView) fragmentView.findViewById(R.id.gridViewTags);
			TagAdapter adapter = new TagAdapter(context, R.layout.tag_item, tags, SelectType.UNCHECK);
			gridViewTags.setAdapter(adapter);
			etTagSearch.setVisibility(View.GONE);
			gridViewTags.setOnItemClickListener(new TagClickListener(TransType.REMOVE));
			adapter.notifyDataSetChanged();
		}
	}
	
	private void findTagsByName(){
		String searchText = etTagSearch.getText().toString();
		
		ServiceCaller serviceCaller = new ServiceCaller();
		String response = serviceCaller.callTagByNameService(searchText, sharedPref.getString(Constants.USER_ID, ""));
		
		List<Tag> tags =  null;
		try {
			tags = JSONParser.parseTags(response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(tags != null){
			gridViewTags = (GridView) fragmentView.findViewById(R.id.gridViewTags);
			ArrayAdapter<Tag> adapter = new ArrayAdapter<Tag>(context, R.layout.tag_item, tags);
			gridViewTags.setAdapter(adapter);
			etTagSearch.setVisibility(View.VISIBLE);
			gridViewTags.setOnItemClickListener(new TagClickListener(TransType.ADD));
			adapter.notifyDataSetChanged();
		}
	}
	
	public class TagToggleListener implements Button.OnClickListener {

		@Override
		public void onClick(View view) {
			int id = view.getId();
			if(id == R.id.btnTags){
				populateUnTaggedTags();
			}
			else if(id == R.id.btnMyTags){
				populateTaggedTags();
			}
		}
		
	}
	
	public class TagSearchListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			
		}

		@Override
		public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
			if(text.length() > 2){
				findTagsByName();
			} else {
				populateUnTaggedTags();
			}
		}
		
	}
	
}
