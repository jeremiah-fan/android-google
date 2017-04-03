package com.example.jerm.soccerexperiment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;
import java.lang.Math;
import java.util.Random;

/**
 * Created by Jerm on 3/22/2017.
 */

public class DrawShapes extends View {
    private static final int INITIAL_BALL_Y = 0;
    private static final double INITIAL_MOVE_RATE = 0;
    private static final double INITIAL_ACCELERATION = 9.81;

    private Bitmap ball, background;
    private int ballX, ballY, ballRad;
    private int screenWidth, screenHeight;
    private int angle;
    private double velocityX, velocityY, accelerationY;
    private boolean game_over = false;
    private Random rand = new Random();
    private float lastTouchX, lastTouchY;
    private int activePointerId = -1;
    private boolean dragMode = false;

    public DrawShapes(Context context, AttributeSet attrs){
        super(context, attrs);
        ball = BitmapFactory.decodeResource(getResources(),R.drawable.ball); //load a ball image
        background = BitmapFactory.decodeResource(getResources(),R.drawable.sky); //load a background
        ballRad = ball.getWidth() / 2;
        angle = 0;

        //double randDirection = rand.nextDouble() * 2 - 1; // -1 to 1
        velocityX = rand.nextDouble() * 40 - 20; // This can be negative
        velocityY = INITIAL_MOVE_RATE; // This can be negative
        accelerationY = INITIAL_ACCELERATION;
        //velocityY = INITIAL_MOVE_RATE * Math.sqrt(1 - Math.pow(randDirection, 2)); // This should never be negative
    }

    // This is called because Android doesn't know how large our device is.
    // It will calculate the dimensions and give it to us, before onDraw() is called.
    @Override
    public void onSizeChanged (int screenW, int screenH, int origW, int origH) {
        super.onSizeChanged(screenW, screenH, origW, origH);
        screenWidth = screenW;
        screenHeight = screenH;
        background = Bitmap.createScaledBitmap(background, screenWidth, screenHeight, true); //Resize background to fit the screen.

        ballX = rand.nextInt(screenW - ball.getWidth());
        ballY = INITIAL_BALL_Y;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawBitmap(background, 0, 0, null);
        move();
        canvas.save();
        canvas.rotate(angle, ballX + ball.getWidth() / 2, ballY + ball.getHeight() / 2); // Pivot around center of ball
        canvas.drawBitmap(ball, ballX, ballY, null);
        canvas.restore();
        angle = (angle + 2) % 360;

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(!game_over)
        {
            final int action = MotionEventCompat.getActionMasked(event);
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    if (getDistance(ballX + ball.getWidth() / 2.0, ballY + ball.getHeight() / 2.0, event.getX(), event.getY()) < ballRad) {
                        activePointerId = MotionEventCompat.getPointerId(event, 0);
                        dragMode = true;

                        final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId);
                        ballX = (int) MotionEventCompat.getX(event, pointerIndex) - ballRad;
                        ballY = (int) MotionEventCompat.getY(event, pointerIndex) - ballRad;
                        velocityX = 0;
                        velocityY = 0;
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position
                    if (dragMode) {
                        final int pointerIndex = MotionEventCompat.findPointerIndex(event, activePointerId);
                        ballX = (int) MotionEventCompat.getX(event, pointerIndex) - ballRad;
                        ballY = (int) MotionEventCompat.getY(event, pointerIndex) - ballRad;
                        invalidate();
                    }
                    break;
                }

                case MotionEvent.ACTION_UP: {
                    activePointerId = -1;
                    dragMode = false;
                    break;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void init(){
        //double randDirection = rand.nextDouble() * 2 - 1; // -1 to 1
        velocityX = rand.nextDouble() * 20 - 10; // This can be negative
        velocityY = INITIAL_MOVE_RATE; // This can be negative
        accelerationY = INITIAL_ACCELERATION;
        //velocityY = INITIAL_MOVE_RATE * Math.sqrt(1 - Math.pow(randDirection, 2)); // This should never be negative

        ballX = rand.nextInt(screenWidth - ball.getWidth());
        ballY = INITIAL_BALL_Y;
        angle = 0;
        game_over = false;
        invalidate();
    }

    private void move(){
        accelerationY -= 0.01f;
        //Log.d("Acceleration", "" + accelerationY);
        velocityX *= 0.999;
        velocityY += accelerationY; // Acceleration
        //Log.d("Velocity Before Negate", "" + velocityY);
        ballX = velocityX > 0 ? Math.min(screenWidth - ball.getWidth(), (int) (ballX + velocityX)) : Math.max(0, (int) (ballX + velocityX));
        ballY = velocityY > 0 ? Math.min(screenHeight - ball.getHeight(), (int) (ballY + velocityY)) : Math.max(0, (int) (ballY + velocityY));
        checkCollisions();
    }

    private void checkCollisions(){
        if(ballX == screenWidth - ball.getWidth() || ballX == 0)
            velocityX *= -1;

        if((ballY == screenHeight - ball.getHeight() && velocityY > 0) || (ballY == 0 && velocityY > 0)) {
            velocityY *= -1;
            accelerationY = INITIAL_ACCELERATION;
            //Log.d("Velocity After Negate", "" + velocityY);
        }
    }

    private double getDistance(double ballCenterX, double ballCenterY, double x, double y){
        return Math.sqrt(Math.pow(ballCenterX - x, 2) + Math.pow(ballCenterY - y, 2));
    }
}
