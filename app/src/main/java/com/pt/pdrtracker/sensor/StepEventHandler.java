package com.pt.pdrtracker.sensor;

import android.os.Handler;
import android.os.Message;

import com.pt.pdrtracker.log.LogRecorder;
import com.pt.pdrtracker.pojo.StepPosition;
import com.pt.pdrtracker.tool.WaveRecorder;

import java.util.Date;


/**
 * Handling step information
 * */
public class StepEventHandler {
    private final Handler activityHandler;

    public StepEventHandler(Handler activityHandler) {
        this.activityHandler = activityHandler;
    }

    /**
     * Calculate the direction and distance of this step
     * Of course, the numbers are relative
     * */
    public void onStep() {
        float angle = (float) OrientationData.getInstance().getAzimuth();
        float distance = getStepSize();
        StepPosition stepPosition = new StepPosition(new Date().getTime(), (float) -Math.cos(angle) * distance, (float) -Math.sin(angle) * distance);
        Message message = new Message();
        message.obj = stepPosition;
        LogRecorder.getInstance().i("stepPosition", stepPosition.toString());
        activityHandler.sendMessage(message);
    }

    /**
     * Calculate this step size according to the formula
     * */
    private float getStepSize() {
        float a = (float) Math.pow(WaveRecorder.getInstance().calculateAndReset(), 0.25);
        float c = 0.5f;
        return a * c;
    }
}
