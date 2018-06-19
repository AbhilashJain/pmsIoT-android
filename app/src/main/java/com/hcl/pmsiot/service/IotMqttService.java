package com.hcl.pmsiot.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.hcl.pmsiot.R;
import com.hcl.pmsiot.constant.PmsConstant;
import com.hcl.pmsiot.data.NotificationData;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class IotMqttService extends Service {

    private MqttHelper mqttHelper;

    private String sapId ;

    private List<String> subscribeTopicList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sapId = intent.getStringExtra("sapId");
        subscribeTopicList = new ArrayList<>();
        subscribeTopicList.add(MessageFormat.format(PmsConstant.userNotificationTopic, sapId));
        startMqtt(subscribeTopicList);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void startMqtt(List<String> subscribeTopics){

        mqttHelper = new MqttHelper(getApplicationContext(), subscribeTopics);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage){
                Log.w("Debug",mqttMessage.toString());
                if(topic.equals(MessageFormat.format(PmsConstant.userNotificationTopic, sapId))) {
                    Gson gson = new Gson();
                    NotificationData notificationData = gson.fromJson(mqttMessage.toString(), NotificationData.class);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getApplicationContext())
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle(notificationData.getTitle())
                                    .setContentText(notificationData.getBody());


                    // Gets an instance of the NotificationManager service//

                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify((int)Math.random(), mBuilder.build());
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
