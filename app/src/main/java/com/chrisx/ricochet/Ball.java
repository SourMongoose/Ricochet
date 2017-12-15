package com.chrisx.ricochet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
    public double x, y, size, angle, speed;
    public boolean moving, bounced;
    private Paint p;

    public Ball(double w) {
        x = y = w/2;
        size = w/40;
        angle = 0;

        moving = bounced = false;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);
    }

    public void draw(Canvas c) {
        c.drawCircle((float)x, (float)y, (float)(size/2), p);
    }
}
