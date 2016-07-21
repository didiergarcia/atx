package com.softwaresushi.android.atx;

import android.support.annotation.AnyThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.softwaresushi.android.atx.threading.NetworkyThread;
import com.softwaresushi.android.atx.threading.WorkyThread;
import com.softwaresushi.android.atx.threading.SleepyThread;
import com.softwaresushi.android.atx.threading.ThreadUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = Constants.TAG + "/main";

  private static final int DEFAULT_PRIORITY = Thread.currentThread().getPriority();
  private static final int MAX_PRIORITY     = Thread.MAX_PRIORITY - 1; // Subtract 1 because seek bar is 1 indexed.

  private static final int THREAD_TYPE_SLEEPY = 0;
  private static final int THREAD_TYPE_COUNTY = 1;
  private static final int THREAD_TYPE_NETWORKY = 2;


  @BindView(R.id.seekbar_thread_priority)
  SeekBar mThreadPrioritySeekBar;
  @BindView(R.id.text_seekbar_value)
  TextView mThreadPriorityTextView;
  @BindView(R.id.use_thread_kick_start_checkbox)
  CheckBox mUseThreadKickStartCheckBox;
  @BindView(R.id.thread_type_radio_buttons)
  RadioGroup mThreadTypeRadioGroup;
  @BindView(R.id.thread_type_sleepy_radio_button)
  RadioButton mThreadTypeSleepyRadioButton;

  boolean mUseThreadKickStart = false;
  int mThreadType = THREAD_TYPE_SLEEPY;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupUi();
  }

  private void setupUi() {
    // Initiate ButterKnife bindings
    ButterKnife.bind(this);

    // Setup seekbar and textview.
    mThreadPrioritySeekBar.setMax(MAX_PRIORITY);
    mThreadPrioritySeekBar.setProgress(DEFAULT_PRIORITY - 1); // Subtract 1 because seekBar is +1 indexed
    mThreadPriorityTextView.setText(String.valueOf(DEFAULT_PRIORITY));
    mThreadPrioritySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged(seekBar..., progress: " + progress + ", fromUser: " + fromUser + ")");

        mThreadPriorityTextView.setText(String.valueOf(progress + 1));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch(seekBar...)");
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch(seekBar...)");
      }
    });


    mThreadTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

        if (checkedId == R.id.thread_type_sleepy_radio_button)
          mThreadType = THREAD_TYPE_SLEEPY;
        else if (checkedId == R.id.thread_type_county_radio_button)
          mThreadType = THREAD_TYPE_COUNTY;
        else if (checkedId == R.id.thread_type_networky_radio_button)
          mThreadType = THREAD_TYPE_NETWORKY;
        else
          throw new IllegalArgumentException("Thread type not known.");

        Log.i(TAG, "Thread type selected: " + mThreadType);
      }
    });
    mThreadTypeSleepyRadioButton.setChecked(true);
  }

  @OnCheckedChanged(R.id.use_thread_kick_start_checkbox)
  public void useThreadKickStartCheckChanged(CheckBox checkBox) {
    Log.i(TAG, "use thread kick-start? " + String.valueOf(checkBox.isChecked()));
    mUseThreadKickStart = checkBox.isChecked();
  }

  @OnClick(R.id.button_start_thread)
  public void startThreadOnClick() {
    int priority = mThreadPrioritySeekBar.getProgress() + 1;

    startJavaThread(priority);
  }

  @OnClick(R.id.lower_ui_thread_priority_button)
  public void lowerUiPriorityOnClick() {

    Thread.currentThread().setPriority(1);
  }

  @AnyThread
  public void startJavaThread(int priority) {

    Thread t = null;
    if (mThreadType == THREAD_TYPE_SLEEPY)
      t = new SleepyThread();
    else if (mThreadType == THREAD_TYPE_COUNTY)
      t = new WorkyThread();
    else if (mThreadType == THREAD_TYPE_NETWORKY) {
      t = new NetworkyThread(this);
    }

    if (t == null) {
      Log.e(TAG, "Could not build thread for type: " + mThreadType);
      return;
    }

    Log.i(TAG, "Starting Thread: " + t.toString());

    if (mUseThreadKickStart) {
      ThreadUtils.threadKickStart(t, priority);
    } else {
      t.setPriority(priority);
      t.start();
    }
  }
}
