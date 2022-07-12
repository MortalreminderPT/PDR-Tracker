package com.pt.pdrtracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.pt.pdrtracker.databinding.ActivityFullscreenBinding;
import com.pt.pdrtracker.log.LogRecorder;
import com.pt.pdrtracker.pojo.StepPosition;
import com.pt.pdrtracker.sensor.StepEventHandler;
import com.pt.pdrtracker.sensor.StepEventListener;
import com.pt.pdrtracker.tool.Permission;

/**
 * Interactive interface, only responsible for drawing
 */
public class MainActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mCanvas.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mCanvas.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };

    private View mControlsView;
    private CanvasView mCanvas;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                    clearView();
                    hide();
                break;
            case MotionEvent.ACTION_UP:
                view.performClick();
                break;
            default:
                break;
        }
        return false;
    };

    private void clearView() {
        mCanvas.clearPoints();
    }

    private ActivityFullscreenBinding binding;
    private StepEventHandler stepEventHandler;
    private StepEventListener stepEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Permission permission = new Permission();
        permission.checkPermissions(this);

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mCanvas = binding.canvasView;

        // Inject context to LogRecorder
        LogRecorder.getInstance(getApplicationContext());

        // Pass StepPosition to CanvasView
        Handler positionHandler = new Handler(message -> {
            StepPosition position = (StepPosition) message.obj;
            mCanvas.addPoint(position.getDx(), position.getDy());
            return true;
        });

        // start a thread to listen and handle the information of sensor
        new Thread(() -> {
            stepEventHandler = new StepEventHandler(positionHandler);
            stepEventListener = new StepEventListener((SensorManager) MainActivity.this.getSystemService(Context.SENSOR_SERVICE), stepEventHandler);
            stepEventListener.start();
        }).start();
        mCanvas.addPoint(0,0);

        mCanvas.setOnClickListener(view -> toggle());

        binding.dummyButton.setOnTouchListener(mDelayHideTouchListener);

    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mCanvas.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mCanvas.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}