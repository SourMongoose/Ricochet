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

    private Ball ball;
    private Dot dot;
    private boolean wentOffScreen;
    private int score;

    //frame data
    private static final int FRAMES_PER_SECOND = 60;
    private long nanosecondsPerFrame;
    private long millisecondsPerFrame;

    private float lastX, lastY;

    private final int bg = Color.rgb(161,214,226);

    private Paint wall, white, spaced, arrow;

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

        //initialize objects
        ball = new Ball(w(),h());
        dot = new Dot(w(),h());
        dot.changeLocation(ball);

        lastX = lastY = 0;

        nanosecondsPerFrame = (long)1e9 / FRAMES_PER_SECOND;
        millisecondsPerFrame = (long)1e3 / FRAMES_PER_SECOND;

        //initialize fonts
        trebuchetms = Typeface.createFromAsset(getAssets(), "fonts/TrebuchetMS.ttf");

        //background
        canvas.drawColor(bg);

        //pre-defined paints
        wall = newPaint(Color.WHITE);
        wall.setStyle(Paint.Style.STROKE);
        wall.setStrokeWidth(c400(3));

        white = newPaint(Color.WHITE);
        white.setStyle(Paint.Style.FILL);

        arrow = new Paint(wall);
        arrow.setStrokeWidth(c400(2));

        spaced = newPaint(Color.WHITE);
        spaced.setTextAlign(Paint.Align.CENTER);

        //define margin
        margin = (h() - w()) / 2;

        //title screen
        canvas.save();
        canvas.translate(0,margin);
        drawTitleMenu();
        canvas.restore();

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
                            if (!paused) {
                                if (menu.equals("started")) {
                                    canvas.drawColor(bg);

                                    ball.draw(canvas);
                                    dot.draw(canvas);

                                    //direction arrow
                                    if (!ball.moving) drawArrow();

                                    //moving the ball
                                    if (ball.moving) {
                                        ball.move();
                                        double dist = Math.sqrt((ball.x-dot.x)*(ball.x-dot.x)
                                                +(ball.y-dot.y)*(ball.y-dot.y));

                                        if (ball.bounced) {
                                            //leaves screen
                                            if (ball.x < -ball.size || ball.x > w()+ball.size
                                                    || ball.y < -ball.size || ball.y > h()+ball.size) {
                                                menu = "gameover";
                                                wentOffScreen = true;
                                            }
                                            //hits dot
                                            if (dist < (ball.size+dot.size)/2) {
                                                score++;

                                                ball.moving = false;
                                                ball.bounced = false;

                                                dot.changeLocation(ball);

                                                if (dot.size > c400(10)) dot.size -= c400(1);
                                            }
                                        } else {
                                            //bounce off walls
                                            if (ball.x < ball.size/2 || ball.x > w()-ball.size/2) {
                                                ball.bounced = true;
                                                ball.angle = Math.PI - ball.angle;
                                            } else if (ball.y < ball.size/2 || ball.y > h()-ball.size/2) {
                                                ball.bounced = true;
                                                ball.angle *= -1;
                                            }
                                            //hits green dot prematurely
                                            if (dist < (ball.size+dot.size)/2) {
                                                menu = "gameover";
                                                wentOffScreen = false;
                                            }
                                        }
                                    }

                                    //score
                                    spacedText(score+"",c400(200),c400(20),c400(25));
                                } else if (menu.equals("gameover")) {
                                    canvas.drawColor(bg);
                                    canvas.save();
                                    canvas.translate(0,margin);
                                    drawGameOverScreen();
                                    canvas.restore();
                                    menu = "limbo";
                                }

                                //update canvas
                                ll.invalidate();
                            }
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

        if (menu.equals("start")) {
            if (action == MotionEvent.ACTION_UP) {
                menu = "started";
            }
        } else if (menu.equals("started")) {
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                lastX = X;
                lastY = Y;
            } else if (action == MotionEvent.ACTION_UP) {
                if (!ball.moving) {
                    ball.angle = Math.atan2(ball.y-Y,ball.x-X);
                    ball.moving = true;
                }
            }
        } else if (menu.equals("limbo")) {
            if (action == MotionEvent.ACTION_UP) {
                menu = "started";
                score = 0;

                ball.x = w()/2;
                ball.y = h()/2;
                ball.moving = ball.bounced = false;

                dot.size = w()/10;
                dot.changeLocation(ball);
            }
        }

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
        spaced.setColor(Color.argb(50,0,0,0));
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
        spacedText("ricochet", c400(200), c400(98), c400(55));

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
        canvas.drawCircle(c400(250),c400(175),c400(5),white);

        spacedText("tap to",c400(200),c400(283),c400(33));
        spacedText("start",c400(200),c400(317),c400(33));
    }

    private void drawGameOverScreen() {
        spacedText("game",c400(200),c400(54),c400(70));
        spacedText("over",c400(200),c400(115),c400(70));

        if (wentOffScreen) {
            spacedText("you missed",c400(200),c400(190),c400(20));
            spacedText("your ricochet!",c400(200),c400(210),c400(20));
        } else {
            spacedText("you hit the blue",c400(200),c400(190),c400(20));
            spacedText("dot too early!",c400(200),c400(210),c400(20));
        }

        spacedText("you scored: "+score,c400(200),c400(257),c400(25));

        spacedText("play again?",c400(200),c400(330),c400(35));
    }

    private void drawArrow() {
        double angle = Math.atan2(ball.y-lastY,ball.x-lastX);
        canvas.drawLine((float)(ball.x+c400(15)*Math.cos(angle)),(float)(ball.y+c400(15)*Math.sin(angle)),
                (float)(ball.x+c400(30)*Math.cos(angle)),(float)(ball.y+c400(30)*Math.sin(angle)),arrow);
        canvas.drawLine((float)(ball.x+c400(30)*Math.cos(angle)),(float)(ball.y+c400(30)*Math.sin(angle)),
                (float)(ball.x+c400(25)*Math.cos(angle-toRad(10))),(float)(ball.y+c400(25)*Math.sin(angle-toRad(10))),arrow);
        canvas.drawLine((float)(ball.x+c400(30)*Math.cos(angle)),(float)(ball.y+c400(30)*Math.sin(angle)),
                (float)(ball.x+c400(25)*Math.cos(angle+toRad(10))),(float)(ball.y+c400(25)*Math.sin(angle+toRad(10))),arrow);
    }
}
