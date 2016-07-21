package com.softwaresushi.android.atx.threading;

import android.support.annotation.NonNull;
import android.util.Log;

import com.softwaresushi.android.atx.Constants;

/**
 * Created by dgarcia on 7/19/16.
 */
public class ThreadUtils {

  private static final String TAG = Constants.TAG + "/tu";
  public static final int THREAD_KICK_START_SLEEP = 7000;

  public static String toPrettyString(@NonNull Thread thread) {
    StringBuilder sb = new StringBuilder();

    if (thread != null) {
      sb.append(thread.getName());
      sb.append(" (Priority: ");
      sb.append(thread.getPriority());
      sb.append(", Group: ");
      sb.append(thread.getThreadGroup().getName());
      sb.append(")");
    }

    return sb.toString();
  }

  public static void threadKickStart(final Thread target, final int priority) {
    Thread t = new Thread(new Runnable() {
      @Override
      public void run() {

        // Add some sleep to be able to log this thread...
        try {
          Thread.sleep(THREAD_KICK_START_SLEEP);
        } catch (InterruptedException ie) {
          Log.w(TAG, "Thread from Thread Starter Thread interrupted :/");
        }

        target.setPriority(priority);
        target.start();

        // Add some sleep to be able to log this thread...
        try {
          Thread.sleep(THREAD_KICK_START_SLEEP);
        } catch (InterruptedException ie) {
          Log.w(TAG, "Thread from Thread Starter Thread interrupted :/");
        }
      }
    });
    t.setPriority(priority);
    t.start();
  }
}
