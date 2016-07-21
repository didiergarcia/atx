package com.softwaresushi.android.atx.threading;

import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.softwaresushi.android.atx.Constants;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dgarcia on 7/18/16.
 */
public class SleepyThread extends Thread {

  private static final String TAG = Constants.TAG + "/trd/slpy";
  private static AtomicInteger mThreadCounter = new AtomicInteger(0);
  private static final int DEFAULT_SLEEP_TIME = 30000; // 30 secs.
  private static final String NAME_PREFIX = "Sleepy-";

  private long mSleepTime = DEFAULT_SLEEP_TIME;

  public SleepyThread() {
    super(NAME_PREFIX + mThreadCounter.getAndIncrement());
  }

  public void setSleepTime(long time) {
    mSleepTime = time;
  }

  public long getSleepTime() {
    return mSleepTime;
  }

  @Override
  public void run() {
    String threadPrettyString = ThreadUtils.toPrettyString(this);
    Log.i(TAG, "Running " + threadPrettyString);

    try {
      Thread.sleep(mSleepTime);
    } catch (InterruptedException ie) {
      Log.w(TAG, "Interrupted " + threadPrettyString);
    }

    Log.i(TAG, "Finished " + threadPrettyString);
  }

  @Override
  public String toString() {
    return ThreadUtils.toPrettyString(this);
  }
}
