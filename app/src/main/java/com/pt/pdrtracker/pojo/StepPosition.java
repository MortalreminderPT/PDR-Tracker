package com.pt.pdrtracker.pojo;

public class StepPosition {
    private Long time;
    private Float dx;
    private Float dy;

    public StepPosition(Long time, Float dx, Float dy) {
        this.time = time;
        this.dx = dx;
        this.dy = dy;
    }

    public Float getDx() {
        return dx;
    }

    public Float getDy() {
        return dy;
    }

    @Override
    public String toString() {
        return "StepPosition{" +
                "time=" + time +
                ", dx=" + dx +
                ", dy=" + dy +
                '}';
    }
}