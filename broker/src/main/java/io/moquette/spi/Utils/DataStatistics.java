package io.moquette.spi.Utils;

import java.util.concurrent.atomic.AtomicLong;

public class DataStatistics {

    public static String PassThrough=null;


    public static AtomicLong OnlineCount=new AtomicLong();
    public static AtomicLong UnlineCount=new AtomicLong();
    public static AtomicLong ActiveCount=new AtomicLong();
    public static AtomicLong PublishSize=new AtomicLong();
    public static AtomicLong SUBSize=new AtomicLong();
    public enum ONlineStatus {
        SUC, FAIL
    }
    public enum ONlineFailMag {
        Login,client_is_null,MQTT_protocol_version_is_not_valid
    }
    public enum DeviceStatus {
        Online,Unonline
    }
}
