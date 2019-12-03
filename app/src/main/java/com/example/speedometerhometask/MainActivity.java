package com.example.speedometerhometask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.SeekBar;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class MainActivity extends AppCompatActivity implements Runnable {
    private VelocityTracker mVelocityTracker = null;
    float saved_x = 0;
    float saved_y = 0;
    int progressFromMove = 0;
    Random rand = new Random();
    SpeedometerView speedometerView;
    Timer timer = new Timer();
    Decrease decreaseClass = new Decrease();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedometerView = findViewById(R.id.speedometer_view);
        final SeekBar seekBar = findViewById(R.id.seekbar_progress);
        seekBar.setMax(speedometerView.getMAX_SPEED());
        run();
        seekBar.setProgress(speedometerView.getProgress());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speedometerView.setProgress(progress);
                progressFromMove = progress;
                decreaseClass.setDecrease(false);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case (ACTION_DOWN):
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(event);
                decreaseClass.setDecrease(false);
                return true;
            case (ACTION_MOVE):
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                changeProgress(mVelocityTracker.getXVelocity(),
                        mVelocityTracker.getYVelocity());
                return true;
            case (ACTION_CANCEL):
            case (ACTION_UP):
                saved_y = 0;
                saved_x = 0;
                decreaseClass.setDecrease(true);
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                return true;
            default:
                return super.onTouchEvent(event);

        }
    }

    private void changeProgress(float current_x, float current_y) {
        float first_speed = (float) Math.sqrt(saved_x * saved_x + saved_y * saved_y);
        float second_speed = (float) Math.sqrt(current_x * current_x + current_y * current_y);
        if (second_speed > first_speed) {
            progressFromMove += rand.nextInt(6);
        }
        saved_x = current_x;
        saved_y = current_y;

        if (progressFromMove < 0) {
            progressFromMove = 0;
        }
        if (progressFromMove > speedometerView.getMAX_SPEED()) {
            progressFromMove = speedometerView.getMAX_SPEED();
        }
        speedometerView.setProgress(progressFromMove);
    }

    @Override
    public void run() {
        timer.schedule(decreaseClass, 0, 100);
    }
    class Decrease extends TimerTask {
        boolean decrease =false;
        public void setDecrease(boolean decrease) {
            this.decrease = decrease;
        }
        @Override
        public void run() {
            if (decrease) {
                int toDecrease = speedometerView.getProgress() - rand.nextInt(5) - 1;
                if (toDecrease < 0) {
                    toDecrease = 0;
                }
                speedometerView.setProgress(toDecrease);
                progressFromMove = toDecrease;
            }

        }
    }
}
