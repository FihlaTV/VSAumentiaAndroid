package com.aumentia.vs.helloVisualSearch;

/**
 * com.aumentia.vs.helloVisualSearch
 * HelloVisualSearch
 * <p/>
 * Created by Pablo GM on 09/03/15.
 * Copyright (c) 2015 Aumentia Technologies. All rights reserved.
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.aumentia.vs.visualsearchsdk.API.VSAumentia;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

class PoolLoader extends AsyncTask<Void, Void, Boolean>
{
    private Context context;

    private VSAumentia vsAumentia;

    private HashMap<Integer, String> imgRes     = new HashMap<>();

    private ProgressDialog progressDialog;

    /**
     * Default constructor
     * @param context Parent context
     * @param vsAumentia Visual Search engine instance
     * @param list List that stores the (image - "text to show") relation
     */
    public PoolLoader(Context context, VSAumentia vsAumentia, HashMap<Integer, String> list)
    {
        this.context    = context;
        this.vsAumentia = vsAumentia;
        this.imgRes     = list;
    }

    /**
     * Add image to the pool from resources.
     * @param imageResourceId Resource Id
     * @param title Image name
     * @return Image uuid, -1 if image not added.
     */
    private int insertImageFromResources(int imageResourceId, String title)
    {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), imageResourceId);

        int imagePool_Id;

        imagePool_Id = vsAumentia.insertImage(bmp);

        if(imagePool_Id != -1)
        {
            imgRes.put(imagePool_Id,title);

            Log.i(HelloVisualSearch.HELLO_TAG, "image " + title + " added to the pool with id: " + imagePool_Id);
        }
        else
        {
            Log.i(HelloVisualSearch.HELLO_TAG,"image " + title + " not added to the pool");
        }

        bmp.recycle();

        return imagePool_Id;
    }

    /**
     * Add image to the pool from assets
     * @param path Path in the assets folder
     * @param title Image name
     * @return Image uuid, -1 if image not added.
     */
    private int insertImageFromAssets(String path,String title)
    {
        int imagePool_Id ;

        AssetManager assetManager = context.getAssets();

        InputStream istr;

        Bitmap bitmap = null;

        try
        {
            istr = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(istr);
        }
        catch (IOException e)
        {
            Log.e(HelloVisualSearch.HELLO_TAG, e.getMessage());
        }

        imagePool_Id = vsAumentia.insertImage(bitmap);

        if(imagePool_Id != -1)
        {
            imgRes.put(imagePool_Id,title);

            Log.i(HelloVisualSearch.HELLO_TAG,"image " + path + "  added to the pool with id: " + imagePool_Id);
        }
        else
        {
            Log.i(HelloVisualSearch.HELLO_TAG,"image " + path + " not added to the pool");
        }

        return imagePool_Id;
    }

    /**
     * Create and show the progress view
     */
    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(context, "Loading...", "Adding Images to the Pool");
    }

    /**
     * Add the images to the lib.
     * Wait until the activity has been resumed.
     */
    @Override
    protected Boolean doInBackground(Void... params)
    {
        //Adding Images

        // Add image from local resources
        insertImageFromResources(R.drawable.pic4, "Church");
        insertImageFromResources(R.drawable.pic5, "Flowers");
        insertImageFromResources(R.drawable.pic6, "Dog");

        // Add image from assets
        insertImageFromAssets("pic1.jpg", "Camera");
        insertImageFromAssets("pic2.jpg", "Train");
        insertImageFromAssets("pic3.jpg", "Moon");

        return true;
    }

    /**
     * Dismiss the ProgressView.
     */
    @Override
    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);

        if(progressDialog.isShowing())
            progressDialog.dismiss();

        // Start matching process
        vsAumentia.start();
    }
}