package com.chrisx.ricochet;

import android.graphics.Color;
import android.graphics.Paint;

public class Dot {
    private double x, y, size;
    private double w; //canvas width
    private Paint p;

    private Dot(double w) {
        size = w/10;
        x = Math.random()*(w - size) + size/2;
        y = Math.random()*(w - size) + size/2;

        this.w = w;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.rgb(34,222,24));
    }

    public void changeLocation(Ball b) {
        double dist;
        do {
            x = Math.random()*(w - size) + size/2;
            y = Math.random()*(w - size) + size/2;
            dist = Math.sqrt((b.x-x)*(b.x-x) + (b.y-y)*(b.y-y));
        } while (dist <= b.size + size);
    }
}
