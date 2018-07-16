package com.bot48.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.bot48.wavebubble.DoubleWaveBubbleView;

public class SimpleDemoActivity extends AppCompatActivity {


    DoubleWaveBubbleView doubleWaveBubbleView;
    RandomHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_demo);
        doubleWaveBubbleView = findViewById(R.id.bubble);
        handler = new RandomHandler();
        Thread thread = new Thread(randomRunnable);
        thread.start();
    }

    class RandomHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int random = (int) msg.obj;
            doubleWaveBubbleView.setPercent(random);
        }
    }

    Runnable randomRunnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                int random = (int) (Math.random()*100);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.obj = random;
                handler.sendMessage(message);
            }
        }
    };
}
