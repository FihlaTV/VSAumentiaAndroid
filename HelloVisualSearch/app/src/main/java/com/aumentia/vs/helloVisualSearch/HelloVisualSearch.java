package com.aumentia.vs.helloVisualSearch;

/**
 * com.aumentia.vs.helloVisualSearch
 * HelloVisualSearch
 * <p/>
 * Created by Pablo GM on 09/03/15.
 * Copyright (c) 2015 Aumentia Technologies. All rights reserved.
 */

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aumentia.vs.visualsearchsdk.API.OnImageMatched;
import com.aumentia.vs.visualsearchsdk.API.OnQRScanned;
import com.aumentia.vs.visualsearchsdk.API.ROI;
import com.aumentia.vs.visualsearchsdk.API.VSAumentia;

import java.util.ArrayList;
import java.util.HashMap;

public class HelloVisualSearch extends Activity implements OnImageMatched, OnQRScanned
{
    //--- GLOBAL VARIABLES -------------------------------------------------------------------------

    // Layout to place the camera
    private FrameLayout             frame;

    // VS instance
    private VSAumentia vsAumentia;

    // TextView to display the matched result
    private TextView                textView    = null;

    // HashMap to store the relation image - text
    private HashMap<Integer, String> imgRes     = new HashMap<>();

    // App API_KEY
    private static final String     API_KEY     = "a6a4026a1523a975dbe0a84e1666c851d85d8ae4";

    // Log debug tag
    public static final String     HELLO_TAG    = "HelloVisualSearch";

    //----------------------------------------------------------------------------------------------

    // *********************************
    // * Life Cycle                    *
    // *********************************

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Get full screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int PreviewSizeWidth   = dm.widthPixels;
        int PreviewSizeHeight  = dm.heightPixels;

        // FrameLayout where we will add the camera view
        frame = (FrameLayout) findViewById(R.id.cameraFrameId);

        // Init Visual Search engine
        vsAumentia = new VSAumentia(this, API_KEY, VSAumentia.SCREEN_ORIENTATION_PORTRAIT, PreviewSizeWidth, PreviewSizeHeight, true, ImageFormat.NV21, frame);

        // Set mathing type ( QR codes and Images )
        vsAumentia.setMatchingType(VSAumentia.IMAGE_QR_MATCHER_MODE);

        // Filter results
        vsAumentia.enableFilter(true);

        // Only images with a score of 5 or bigger will be added to the matching pool
        vsAumentia.setRecognitionThreshold(5);

        // Register callbacks
        vsAumentia.setImageRecognitionCallback( this );
        vsAumentia.setQRRecognitionCallback(this);

        // Add text view to show the matched image info
        addTextView();

        // Start adding images to the pool
        addImages();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.d(HELLO_TAG, "*** onStart() *** ");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        vsAumentia.start();

        Log.d(HELLO_TAG, "*** onResume() *** ");
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Log.d(HELLO_TAG, "*** onStop() ***");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        // UnRegister callback
        vsAumentia.setImageRecognitionCallback( null );

        vsAumentia.stop();

        vsAumentia.release();

        System.gc();

        Log.d(HELLO_TAG, "*** onDestroy() ***");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        vsAumentia.stop();

        Log.d(HELLO_TAG, "*** onPause() ***");
    }

    // *********************************
    // * ADD IMAGES                    *
    // *********************************

    /**
     * Add images to the matching pool
     */
    private void addImages()
    {
        PoolLoader poolLoader = new PoolLoader(this, vsAumentia, imgRes);

        poolLoader.execute((Void)null);
    }

    // *********************************
    // * VS CALLBACK                   *
    // *********************************
    @Override
    public void onImageMatchedResult(int result)
    {
        if (result != -1)
        {
            textView.setText(imgRes.get(result));
        }
        else
        {
            textView.setText("No Match");
        }
    }

    @Override
    public void onSingleQRScanned(String result)
    {
        if (!result.equals(""))
        {
            textView.setText(result);
        }
        else
        {
            textView.setText("No Match");
        }
    }

    @Override
    public void onMultipleQRScanned(ArrayList<ROI> roiList) {
        // TODO: pending feature
    }


    // *********************************
    // * SHOW RESULTS                  *
    // *********************************

    /**
     * Display text view with the matching result
     */
    private void addTextView()
    {
        textView = new TextView(this);

        textView.setTextColor(Color.LTGRAY);

        textView.setTextSize(30);

        FrameLayout.LayoutParams frame = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        addContentView(textView,frame);
    }
}
