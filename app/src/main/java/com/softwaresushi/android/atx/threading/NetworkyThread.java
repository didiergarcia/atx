package com.softwaresushi.android.atx.threading;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.softwaresushi.android.atx.Constants;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dgarcia on 7/20/16.
 */
public class NetworkyThread extends Thread {

  private static final String TAG = Constants.TAG + "/trd/ntwky";

  private static final String DEFAULT_URL = "http://test.softwaresushi.com/slow.html?w=30"; // takes 30 secs to fetch this URL.
  private static final String NAME_PREFIX = "Networky-";
  public static final int TIMEOUT = 35000;

  private static AtomicInteger sThreadCounter = new AtomicInteger();
  private String mUrl = DEFAULT_URL;
  private WeakReference<Context> mWeakContext;

  public NetworkyThread() {
    super(NAME_PREFIX + sThreadCounter.getAndIncrement());
  }

  public NetworkyThread(Context context) {
    this();
    setContext(context);
  }

  public void setContext(Context context) {
    mWeakContext = new WeakReference<Context>(context);
  }

  public void setUrl(String url) {
    mUrl = url;
  }

  @Override
  public void run() {
    String threadPrettyString = ThreadUtils.toPrettyString(this);
    Log.i(TAG, "Started " + threadPrettyString);

    if (mWeakContext != null && mWeakContext.get() != null) {
      ConnectivityManager cm = (ConnectivityManager)mWeakContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo         ni = cm.getActiveNetworkInfo();

      if (ni != null && ni.isConnected()) {
        try {
          URL url = new URL(mUrl);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();

          // NOTE: the timeout has to be longer than it takes to fetch the url.
          conn.setReadTimeout(TIMEOUT);
          conn.setConnectTimeout(TIMEOUT);
          conn.setRequestMethod("GET");

          conn.connect();
          int response = conn.getResponseCode();
          Log.i(TAG, "Got response code: " + response);
        } catch (MalformedURLException mue ) {
          Log.e(TAG, "Url was malformed.", mue);
        } catch (IOException ioe) {
          Log.e(TAG, "Error downloading url.", ioe);
        }
      } else {
        Log.e(TAG, "No active network. :/");
      }
    } else {
      Log.e(TAG, "Lost reference to Activity... :(");
    }

    Log.i(TAG, "Finished " + threadPrettyString);
  }

  @Override
  public String toString() {
    return ThreadUtils.toPrettyString(this);
  }
}

