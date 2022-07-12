package com.pt.pdrtracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * A view used only to draw point coordinates
 * */

public class CanvasView extends View {
    private final Paint paint = new Paint();
    private Canvas canvas = null;
    // All points are recorded in pointList
    private final List<PointF> pointList = new ArrayList<>();

    private final int width;
    private final int height;

    private float x = 0;
    private float y = 0;

    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.canvas = canvas;
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
        for (PointF point : pointList)
            drawPoint(point.x, point.y);
    }

    // add the newly point to pointList
    public void addPoint(float dx, float dy) {
        x += dx;
        y += dy;
        pointList.add(new PointF((float) (width * 0.5 + 10 * x), (float) (height * 0.5 + 10 * y)));
        invalidate();
    }

    // draw all point from pointList
    private void drawPoint(float x, float y) {
        canvas.drawCircle(x, y, 10f, paint);
    }

    // clear the pointList
    public void clearPoints() {
        x = 0;
        y = 0;
        pointList.clear();
        addPoint(0, 0);
        invalidate();
    }
}