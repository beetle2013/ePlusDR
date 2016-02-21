package com.alpha.ePlusDR;

import android.app.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class DrivingRecorder extends Activity {
    /**
     * Called when the activity is first created.
     */

    public static final String RECORD_CTL = "com.alpha.ePlusDR.RECORD_CTL";
    public static final String RECORD_STAT = "com.alpha.ePlusDR.RECORD_STAT";
    public static final String RECORD_NOTIFY ="com.alpha.ePlusDR.RECORD_NOTIFY";

    ActivityReceiver mReceiver = new ActivityReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //前台创建时做一些初始化工作
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        regReceiver();

        Button start = (Button) findViewById(R.id.buttonStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("startButton");
                startService();


            }
        });

        Button stop = (Button) findViewById(R.id.buttonStop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        Button quit = (Button) findViewById(R.id.buttonQuit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button test = (Button) findViewById(R.id.buttonTest);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendControl();
            }
        });


    }

    @Override
    protected void onDestroy() {
        //前台结束时做一些收尾工作
        super.onDestroy();
        unregReceiver();
    }


    //注册广播
    private void regReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(RECORD_STAT);
        registerReceiver(mReceiver, mFilter);
        showToast("registerActivityReceiver");

    }

    //解除广播
    private void unregReceiver() {
        unregisterReceiver(mReceiver);
    }

    //显示一个消息提示框
    private void showToast(String mString) {
        T.showShort(this, mString);
    }

    //启动服务
    private void startService() {
        Intent serviceIntent = new Intent(this, RecorderService.class);
        startService(serviceIntent);
    }

    //停止服务
    private void stopService() {
        Intent serviceIntent = new Intent(this, RecorderService.class);
        stopService(serviceIntent);
    }


    //内部广播接收类
    public class ActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //todo
            //收到服务状态，界面更新
            showToast(intent.getStringExtra("msg"));
        }
    }



    //发送指令到服务
    private void sendControl() {
        Intent mIntent = new Intent(RECORD_CTL);
        sendBroadcast(mIntent);
    }


}
