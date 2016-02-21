package com.alpha.ePlusDR;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by beetle on 2015/2/14.
 */
public class RecorderService extends Service {

    ServiceReceiver mReceiver = new ServiceReceiver();
    NotifyReceiver nReceiver = new NotifyReceiver();

    private NotificationManager manager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //服务创建时做一些初始化工作
        super.onCreate();
        regReceiver();
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendStatus("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
        //服务开始后,收到开始指令,开始录像;收到停止指令,停止录像


    }

    @Override
    public void onDestroy() {
        //服结束时做一些收尾工作
        super.onDestroy();
        unregReceiver();
        dismiss();
        sendStatus("onDestroy");
    }

    //注册广播
    private void regReceiver(){
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(DrivingRecorder.RECORD_CTL);
        registerReceiver(mReceiver, mFilter);
        sendStatus("registerServiceReceiver");
        IntentFilter nFilter = new IntentFilter();
        nFilter.addAction(DrivingRecorder.RECORD_NOTIFY);
        registerReceiver(nReceiver,nFilter);
        sendStatus("registerNotifyReceiver");
    }

    //解除广播
    private void unregReceiver(){
        unregisterReceiver(mReceiver);
        unregisterReceiver(nReceiver);

    }

    //内部广播接收类
    public class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //todo
            //收到前台指令,执行相应操作
            sendStatus("onReceive");
            sendNotify2();

        }
    }

    //通知广播接收类
    public class NotifyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //收到通知事件,做出响应
            sendStatus("notification oNclick");
        }
    }

    //发送状态信息给前台
    private void sendStatus(String mString){
        Intent mIntent = new Intent(DrivingRecorder.RECORD_STAT);
        mIntent.putExtra("msg",mString);
        sendBroadcast(mIntent);
    }

    //发送通知
    private void sendNotify() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.car)
                        .setContentTitle(getResources().getString(R.string.recording))
                        .setContentText(getResources().getString(R.string.recorded));

        sendStatus("sendNotify");


        Intent resultIntent = new Intent(this, DrivingRecorder.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DrivingRecorder.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        // mId allows you to update the notification later on.
        manager.notify(0, mBuilder.build());
    }

    public void sendNotify2() {
        //remote view to to add to notification
        RemoteViews rmVs = new RemoteViews("com.alpha.ePlusDR", R.layout.notify);

        Intent nIntent = new Intent(DrivingRecorder.RECORD_NOTIFY);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, nIntent, 0);

        rmVs.setOnClickPendingIntent(R.id.pause, pi);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.logo)
                        .setContent(rmVs);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, DrivingRecorder.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(DrivingRecorder.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId (my id)  allows you to update the notification later on.
        int mId = 1234;
        mNotificationManager.notify(mId, mBuilder.build());
    }

    //清除所有通知
    private void dismiss() {
        manager.cancelAll();
    }
}
