package com.infozimo.ui.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    
	ImageView imageView;
    String url;
    
    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        url = urls[0];
        Bitmap mIcon = null;
        
        try {
            InputStream in = new java.net.URL(url).openConnection().getInputStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
    	imageView.setImageBitmap(result);
    }
}