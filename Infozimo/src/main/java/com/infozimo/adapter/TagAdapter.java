package com.infozimo.adapter;

import java.util.List;

import com.infozimo.android.R;
import com.infozimo.beans.Tag;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TagAdapter extends ArrayAdapter<Tag> {

	private int resource;
	private LayoutInflater inflater;
	private SelectType selectType;
	
	public TagAdapter(Context context, int resource, List<Tag> tags, final SelectType selectType) {
		super(context, resource, tags);
		this.resource = resource;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.selectType = selectType;
	}

	@Override
    public View getView(int position, View v, ViewGroup parent) {
		View view = v;
		if(view == null){
			view = inflater.inflate(resource, null);
		}
		
		TextView textView = (TextView) view.findViewById(R.id.tvTagItem);
		textView.setText(getItem(position).toString());
		
		if(selectType != null){
			if(SelectType.CHECK.equals(selectType)){
				textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.select, 0);
			} else {
				textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.close, 0);
			}
		}
		
		return view;
	}
	
	public enum SelectType {
		CHECK, UNCHECK;
	}
	
}
