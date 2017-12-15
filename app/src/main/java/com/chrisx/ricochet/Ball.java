package com.chrisx.ricochet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
    public double x, y, size, angle, speed;
    public boolean moving, bounced;
    private Paint p;

    public Ball(double w, double h) {
        x = w/2;
        y = h/2;
        size = w/40;
        angle = 0;
        speed = w*6/400;

        moving = bounced = false;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);
    }

    public void move() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
    }

    public void draw(Canvas c) {
        c.drawCircle((float)x, (float)y, (float)(size/2), p);
    }
}
