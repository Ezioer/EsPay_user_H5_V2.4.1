package com.zqhy.hyxm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import hdtx.androidsdk.Starter;
import hdtx.androidsdk.callback.OpenAdCallback;

public class SplashActivity extends AppCompatActivity {
    private static final long COUNTER_TIME_MILLISECONDS = 5000;
    private CountDownTimer countDownTimer;
    private long secondsRemaining;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Starter.getInstance().loadOpenAd(this, true, new OpenAdCallback() {
            @Override
            public void onAdShow() {
                countDownTimer.cancel();
            }

            @Override
            public void onAdComplete() {
                countDownTimer.cancel();
                startMainActivity();
            }
        });
        createTimer(COUNTER_TIME_MILLISECONDS);
    }

    private void createTimer(long time) {
        final TextView counterTextView = findViewById(R.id.timer);
        countDownTimer =
                new CountDownTimer(time, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
                        counterTextView.setText("" + secondsRemaining);
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                        counterTextView.setText("Done");
                        startMainActivity();
                    }
                };
        countDownTimer.start();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
