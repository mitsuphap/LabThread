package com.example.labthread;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    int counter;
    TextView tvCounter;

    Thread thread;
    Handler handler;

    HandlerThread backgroundHandlerThread;
    Handler backgroundHandler;
    Handler mainHandler;

    SampleAsyncTask sampleAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counter = 0;
        tvCounter = (TextView) findViewById(R.id.tvCounter);

        //Thread Method 1 : Thread
        /*
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Run in background(Cannot update UI here)
                for (int i = 0; i < 100; i++) {
                    counter++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // UI Thread a.k.a Main Thread
                            tvCounter.setText(counter + "");
                        }
                    });
                }
            }
        });
        thread.start();
         */
        //Thread Method 2 : Thread with Handler
        /*
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //Run in Main Thread
                tvCounter.setText(msg.arg1 + "");
            }
        };
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Run in background (Cannot update UI here)
                for (int i = 0; i < 100; i++) {
                    counter++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                    Message msg = new Message();
                    msg.arg1 = counter;
                    handler.sendMessage(msg);
                }
            }
        });
        thread.start();
        */
        //Thread Method 3 : Handler Only
        /*
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                counter++;
                tvCounter.setText(counter + "");
                if (counter < 100)
                    sendEmptyMessageDelayed(0, 1000);
            }
        };
        handler.sendEmptyMessageDelayed(0, 1000);
         */

        //Thread Method 4 : HandlerThread
        /*
        backgroundHandlerThread = new HandlerThread("BackgroundHandlerThread");
        backgroundHandlerThread.start();

        backgroundHandler = new Handler(backgroundHandlerThread.getLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //Run with background
                Message msgMain = new Message();
                msgMain.arg1 = msg.arg1 + 1;
                mainHandler.sendMessage(msgMain);
            }
        };

        mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                //Run with main Thread
                tvCounter.setText(msg.arg1 + "");
                if (msg.arg1 < 100) {
                    Message msgBack = new Message();
                    msgBack.arg1 = msg.arg1;
                    backgroundHandler.sendMessageDelayed(msgBack,1000);
                }
            }
        };
        Message msgBack = new Message();
        msgBack.arg1 = 0; //Start count at 0
        backgroundHandler.sendMessageDelayed(msgBack, 1000);
        */

        // Thread Method 5: AsyncTask
        sampleAsyncTask = new SampleAsyncTask();
        sampleAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0 , 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        thread.interrupt();
//        backgroundHandlerThread.quit();
        sampleAsyncTask.cancel(true);
    }

    class SampleAsyncTask extends AsyncTask<Integer, Float, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
            //Run in Background Thread
            int start = integers[0]; // 0
            int end = integers[1];   // 100
            for (int i = start; i < end; i++){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    return false;
                }
                publishProgress(i + 0.0f);
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            //Run in Main Thread
            super.onProgressUpdate(values);
            float progress = values[0];
            tvCounter.setText(progress + "%");
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            //Run in Main Thread
            super.onPostExecute(aBoolean);
            // work with aBoolean
        }
    }
}