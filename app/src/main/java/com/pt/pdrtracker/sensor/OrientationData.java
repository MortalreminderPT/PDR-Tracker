package com.pt.pdrtracker.sensor;

import android.util.Log;

import java.util.Date;

public class OrientationData {
    private static OrientationData instance;
    long time;
    private double Azimuth;
    private double Pitch;
    private double Roll;

    public static synchronized OrientationData getInstance() {
        if (instance != null) return instance;
        instance = new OrientationData();
        return instance;
    }

    public void update(float[] orientations) {
        this.time = new Date().getTime();
        this.Azimuth = orientations[0];
        this.Pitch = orientations[1];
        this.Roll = orientations[2];
    }

    public long getTime() {
        return time;
    }

    public double getAzimuth() {
        return Azimuth;
    }

    public double getPitch() {
        return Pitch;
    }

    public double getRoll() {
        return Roll;
    }
}