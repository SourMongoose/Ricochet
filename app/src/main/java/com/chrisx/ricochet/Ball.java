package com.chrisx.ricochet;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Ball {
    public double x, y, size, angle, speed;
    public double w, h; //canvas width/height
    public boolean moving, bounced;
    private Paint p;

    public Ball(double w, double h) {
        x = w/2;
        y = h/2;
        size = w/40;
        angle = 0;
        speed = w*6/400;

        this.w = w;
        this.h = h;

        moving = bounced = false;

        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);
    }

    public void reset() {
        x = w/2;
        y = h/2;

        moving = bounced = false;
    }

    public void move() {
        x += Math.cos(angle) * speed;
        y += Math.sin(angle) * speed;
    }

    //check if ball is out of bounds
    public boolean offScreen() {
        return x < -size || x > w+size || y < h/2-w/2-size || y > h/2+w/2+size;
    }
    //check if ball is touching left or right walls
    public boolean touchingLRWalls() {
        return x < size/2 || x > w-size/2;
    }
    //check if ball is touching top or bottom walls
    public boolean touchingUDWalls() {
        return y < h/2-w/2+size/2 || y > h/2+w/2-size/2;
    }

    public void draw(Canvas c) {
        c.drawCircle((float)x, (float)y, (float)(size/2), p);
    }
}
