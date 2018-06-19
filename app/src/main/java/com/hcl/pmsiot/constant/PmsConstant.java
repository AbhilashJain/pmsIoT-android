package com.hcl.pmsiot.constant;

public class PmsConstant {

    public final static String MqttUrl = "tcp://192.168.99.100:1883";

    public final static String dashBoardBaseUrl = "http://192.168.99.100:8074";

    public final static String locationUrl = dashBoardBaseUrl+"/location";

    public final static String userLocationTopic = "user/location";

    public final static String userNotificationTopic = "user/{0}/notification";

    public final static String userNearbyNotificationTopic = "user/{0}/nearby";
}
