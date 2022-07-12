package com.pt.pdrtracker.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.pt.pdrtracker.tool.WaveRecorder;

public class StepEventListener implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor stepDetectorSensor, rotationVectorSensor, linearAccelerationSensor;
    private StepEventHandler stepEventHandler;

    private float[] rotationMatrixFromVector = new float[16];
    private float[] rotationMatrix = new float[16];
    private float[] orientationVals = new float[3];

    public StepEventListener(SensorManager sensorManager, StepEventHandler stepEventHandler) {
        this.sensorManager = sensorManager;
        this.stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        this.stepEventHandler = stepEventHandler;
    }

    public void start() {
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    /**
     * Update the corresponding information to the data object for different sensors
     * */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ROTATION_VECTOR:
                SensorManager.getRotationMatrixFromVector(rotationMatrixFromVector, sensorEvent.values);
                SensorManager.remapCoordinateSystem(rotationMatrixFromVector,
                        SensorManager.AXIS_X, SensorManager.AXIS_Z,
                        rotationMatrix);
                SensorManager.getOrientation(rotationMatrix, orientationVals);
                OrientationData.getInstance().update(orientationVals);
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                float linearAcceleration = (float) Math.sqrt(
                        sensorEvent.values[0] * sensorEvent.values[0]
                                + sensorEvent.values[1] * sensorEvent.values[1]
                                + sensorEvent.values[2] * sensorEvent.values[2]);
                AccelerationMagnitudeData.getInstance().update(linearAcceleration);
                WaveRecorder.getInstance().update(linearAcceleration);
                break;
            case Sensor.TYPE_STEP_DETECTOR:
                stepEventHandler.onStep();
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
