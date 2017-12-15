package com.chrisx.ricochet;

/**
 * Organized in order of priority:
 * @TODO everything
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private Bitmap bmp;
    private Canvas canvas;
    private LinearLayout ll;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private Typeface trebuchetms;

    private boolean paused = false;
    private long frameCount = 0;

    private String menu = "start";
    private float margin;

    //frame data
    private static final int FRAMES_PER_SECOND = 60;
    private long nanosecondsPerFrame;
    private long millisecondsPerFrame;

    private float downX, downY;

    private Paint wall, ball, spaced;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //creates the bitmap
        //note: Star 4.5 is 480x854
        bmp = Bitmap.createBitmap(Resources.getSystem().getDisplayMetrics().widthPixels,
                Resources.getSystem().getDisplayMetrics().heightPixels,
                Bitmap.Config.ARGB_8888);

        //creates canvas
        canvas = new Canvas(bmp);

        ll = (LinearLayout) findViewById(R.id.draw_area);
        ll.setBackgroundDrawable(new BitmapDrawable(bmp));

        //initializes SharedPreferences
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        nanosecondsPerFrame = (long)1e9 / FRAMES_PER_SECOND;
        millisecondsPerFrame = (long)1e3 / FRAMES_PER_SECOND;

        //initialize fonts
        trebuchetms = Typeface.createFromAsset(getAssets(), "fonts/TrebuchetMS.ttf");

        //background
        canvas.drawColor(Color.rgb(141,188,214));

        //pre-defined paints
        wall = newPaint(Color.WHITE);
        wall.setStyle(Paint.Style.STROKE);
        wall.setStrokeWidth(c400(3));

        ball = newPaint(Color.WHITE);
        ball.setStyle(Paint.Style.FILL);

        spaced = newPaint(Color.WHITE);
        spaced.setTextAlign(Paint.Align.CENTER);

        //define margin
        margin = (h() - w()) / 2;

        canvas.save();
        canvas.translate(0,margin);

        //title screen
        drawTitleMenu();

        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //draw loop
                while (!menu.equals("quit")) {
                    long startTime = System.nanoTime();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {


                            //update canvas
                            ll.invalidate();
                        }
                    });

                    frameCount++;

                    //wait until frame is done
                    while (System.nanoTime() - startTime < nanosecondsPerFrame);
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        paused = false;
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    //handles touch events
    public boolean onTouchEvent(MotionEvent event) {
        float X = event.getX();
        float Y = event.getY();
        int action = event.getAction();

        return true;
    }

    //shorthand for w() and h()
    private float w() {
        return canvas.getWidth();
    }
    private float h() {
        return canvas.getHeight();
    }

    //creates an instance of Paint set to a given color
    private Paint newPaint(int color) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(color);
        p.setTypeface(trebuchetms);

        return p;
    }

    private float c400(float f) {
        return w() / 400 * f;
    }
    private float c854(float f) {
        return h() / 854 * f;
    }

    private long getHighScore() {
        return sharedPref.getInt("high_score", 0);
    }

    private double toRad(double deg) {
        return Math.PI/180*deg;
    }

    private void spacedText(String s, double x, double y, double size) {
        spaced.setColor(Color.argb(0,0,0,50));
        spaced.setTextSize((float)size);

        String o = new String();
        for (int i = 0; i < s.length(); i++) {
            o += s.charAt(i);
            o += " ";
        }
        o = o.substring(0,o.length()-1);

        y -= (spaced.ascent() + spaced.descent())/2;

        canvas.drawText(o, (float)(x+size/10), (float)(y+size/10), spaced);
        spaced.setColor(Color.WHITE);
        canvas.drawText(o, (float)x, (float)y, spaced);
    }

    private void drawTitleMenu() {
        spacedText("ricochet", c400(200), c400(100), c400(50));

        //"wall"
        wall.setAlpha(200);
        canvas.drawLine(c400(100),c400(150),c400(300),c400(150),wall);
        //left streak
        for (int i = 0; i < 10; i++) {
            wall.setAlpha(i*25);
            canvas.drawPoint(c400(100+i*10),c400(197-i*22/5),wall);
        }
        //right streak
        for (int i = 0; i < 5; i++) {
            canvas.drawPoint(c400(200+i*10),c400(153+i*22/5),wall);
        }
        //ball
        canvas.drawCircle(c400(250),c400(175),c400(5),ball);

        spacedText("start",c400(200),c400(300),c400(35));
    }
}
