package com.softwaresushi.android.atx.threading;

import android.util.Log;

import com.softwaresushi.android.atx.Constants;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dgarcia on 7/19/16.
 */
public class WorkyThread extends Thread{
  private static final String TAG = Constants.TAG + "/trd/wrky";

  private static AtomicInteger mThreadCounter = new AtomicInteger(0);
  private static final String NAME_PREFIX = "Worky-";
  private static final long DEFAULT_COUNT_MAX = 200_000L;

  private long mCountMax = DEFAULT_COUNT_MAX;

  public WorkyThread() {
    super(NAME_PREFIX + mThreadCounter.getAndIncrement());
  }

  public void setCountMax(long countMax) {
    mCountMax = countMax;
  }

  @Override
  public void run() {
    String threadPrettyString = ThreadUtils.toPrettyString(this);
    Log.i(TAG, "Running " + threadPrettyString);

    AtomicInteger atomicInteger = new AtomicInteger(0);
    SecureRandom rand = new SecureRandom();

    for (int i = 1; i <= mCountMax; i++) {

      UUID.randomUUID();
      atomicInteger.getAndIncrement();
      rand.generateSeed(2048);
      rand.nextGaussian();
      rand.nextBytes(new byte[2048]);
      Math.log(rand.nextFloat());
    }

    Log.i(TAG, "Finished " + threadPrettyString);
  }



  @Override
  public String toString() {
    return ThreadUtils.toPrettyString(this);
  }
}
