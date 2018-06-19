package com.hcl.pmsiot.service;

import android.content.Context;
import android.util.Log;

import com.hcl.pmsiot.PmsMqttCallBack;
import com.hcl.pmsiot.constant.PmsConstant;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

import static android.content.ContentValues.TAG;

public class MqttPublisher {

    MqttAndroidClient client ;
    Context context;

    public MqttPublisher(Context context, final PmsMqttCallBack mqttCallBack, final String subscribeTopic){
        this.context = context;
        this.client = new MqttAndroidClient(context, PmsConstant.MqttUrl,  MqttClient.generateClientId());
        this.client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                if(subscribeTopic != null)
                    subscribeToTopic(subscribeTopic);
            }
            @Override
            public void connectionLost(Throwable throwable) {

            }
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                if(mqttCallBack != null)
                    mqttCallBack.messageArrived(topic, mqttMessage.toString());
                //client.disconnect();
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });

    }
    private double lat;

    private double longitute;

    //private String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    public void publish(final String topic, final String payload) {
        //String clientId = MqttClient.generateClientId();
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    //Log.d(TAG, "Device id"+deviceId);
                    Log.d(TAG, "onSuccess");
                    //String topic = "iot_data";
                    //String payload = "(" + getLat() + "," + getLongitute()+ "," + sapId+")";
                    byte[] encodedPayload = new byte[0];

                    try {
                        encodedPayload = payload.getBytes("UTF-8");

                        MqttMessage message = new MqttMessage(encodedPayload);
                        client.publish(topic, message);

                        //client.disconnect();
                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });

        } catch (  MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribeToTopic(String subscriptionTopic) {
        try {
            this.client.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("Mqtt","Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("Mqtt", "Subscribed fail!");
                }
            });

        } catch (MqttException ex) {
            Log.e(TAG, "Exceptionst subscribing", ex);
            ex.printStackTrace();
        }
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitute() {
        return longitute;
    }

    public void setLongitute(double longitute) {
        this.longitute = longitute;
    }


}
