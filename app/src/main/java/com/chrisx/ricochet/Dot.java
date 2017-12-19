package com.chrisx.ricochet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Dot {
    public double x, y, size;
    private double w, h; //canvas width/height
    private Paint p;

    public Dot(double w, double h) {
        reset();

        this.w = w;
        this.h = h;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.rgb(25,149,173));
    }

    public void reset() {
        size = w*3/20;
        x = y = 0;
    }

    public void changeLocation(Ball b) {
        double dist;
        do {
            x = Math.random()*(w - size) + size/2;
            y = h/2 - w/2 + Math.random()*(w - size) + size/2;
            dist = Math.sqrt((b.x-x)*(b.x-x) + (b.y-y)*(b.y-y));
        } while (dist <= b.size + size);
    }

    public void draw(Canvas c) {
        c.drawCircle((float)x, (float)y, (float)(size/2), p);
    }
}
