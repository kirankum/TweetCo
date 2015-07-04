package com.tweetco.asynctasks;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.imagedisplay.util.AsyncTask;
import com.imagedisplay.util.RecyclingBitmapDrawable;
import com.imagedisplay.util.Utils;
import com.onefortybytes.BuildConfig;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by kirankumar on 04/07/15.
 */
public class BitmapWorkerTask extends AsyncTask<Void, Void, BitmapDrawable>
{
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String TAG = "BitmapWorkerTask";

    private String mUrl;
    private final WeakReference<View> imageViewReference;
    Resources mResources = null;

    public BitmapWorkerTask(String url, View imageView, Activity activity)
    {
        mUrl = url;
        mResources = activity.getResources();
        imageViewReference = new WeakReference<View>(imageView);
    }

    /**
     * Background processing.
     */
    @Override

    protected BitmapDrawable doInBackground(Void... params) {

        Bitmap bitmap = null;
        BitmapDrawable drawable = null;


        // If the bitmap was not found in the cache and this task has not been cancelled by
        // another thread and the ImageView that was originally bound to this task is still
        // bound back to this task and our "exit early" flag is not set, then call the main
        // process method (as implemented by a subclass)
        View imageView = imageViewReference.get();
        if (bitmap == null && !isCancelled() && imageView != null)
        {

            HttpURLConnection urlConnection = null;
            BufferedInputStream in = null;

            try {
                final URL url = new URL(mUrl);
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), IO_BUFFER_SIZE);

                byte[] bytes = IOUtils.toByteArray(in);
                String s = new String(bytes);
                byte[] decodeByteArray = Base64.decode(s, Base64.DEFAULT);
                bitmap = BitmapFactory.decodeByteArray(decodeByteArray, 0, decodeByteArray.length);

            } catch (final IOException e)
            {
                Log.e(TAG, "Error in downloadBitmap - " + e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (final IOException e) {}
            }
        }

        if (bitmap != null)
        {
            if (Utils.hasHoneycomb()) {
                // Running on Honeycomb or newer, so wrap in a standard BitmapDrawable
                drawable = new BitmapDrawable(mResources, bitmap);
            } else {
                // Running on Gingerbread or older, so wrap in a RecyclingBitmapDrawable
                // which will recycle automagically
                drawable = new RecyclingBitmapDrawable(mResources, bitmap);
            }
        }

        return drawable;
    }

    /**
     * Once the image is processed, associates it to the imageView
     */
    @Override
    protected void onPostExecute(BitmapDrawable value)
    {
        if (isCancelled())
        {
            value = null;
        }

        View imageView = imageViewReference.get();
        if (value != null && imageView != null)
        {
            if (BuildConfig.DEBUG)
            {
                Log.d(TAG, "onPostExecute - setting bitmap");
            }
            imageView.setBackgroundDrawable(value);
        }
    }
}
