package com.softwaresushi.android.atx.threading;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.softwaresushi.android.atx.Constants;

import java.io.IOException;
import java.io.InputStream;
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

  private static final String DEFAULT_URL = "http://ipv4.download.thinkbroadband.com/50MB.zip"; // takes 30 secs to fetch this URL.
  private static final String NAME_PREFIX = "Networky-";
  public static final int TIMEOUT = 135000;

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
      InputStream         in = null;

      if (ni != null && ni.isConnected()) {
        try {
          // Add a unique fragment (current time) so that the request isn't cached.
          String urlString = mUrl + "#" + System.currentTimeMillis();
          Log.i(TAG, "Downloading " + urlString);

          URL url = new URL(urlString);
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();

          // NOTE: the timeout has to be longer than it takes to fetch the url.
          conn.setReadTimeout(TIMEOUT);
          conn.setConnectTimeout(TIMEOUT);
          conn.setRequestMethod("GET");
          conn.setDoInput(true);

          conn.connect();
          int response      = conn.getResponseCode();
          int contentLength = conn.getContentLength();

          Log.i(TAG, "Content length: " + contentLength);

          in = conn.getInputStream();

          byte[] data = new byte[2048];
          long total = 0;
          int count;
          while ((count = in.read(data)) != -1) {
            total += count;
          }

          Log.i(TAG, "Downloaded: " + total + " bytes.");
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

