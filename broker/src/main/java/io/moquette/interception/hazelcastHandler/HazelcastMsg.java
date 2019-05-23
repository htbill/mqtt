package io.moquette.interception.hazelcastHandler;

import io.netty.handler.codec.mqtt.MqttMessageType;

import java.io.Serializable;

public class HazelcastMsg implements Serializable {
    private final String clientId;
    private final String userName;
    private final byte qos;
    private final byte[] payload;
    private final String topic;
    private final MqttMessageType MqttMessageType;

    public HazelcastMsg(String clientId, String topic, byte qos, byte[] payload, String userName,MqttMessageType MqttMessageType) {
        this.clientId = clientId;
        this.qos = qos;
        this.payload = payload;
        this.userName = userName;
        this.topic = topic;
        this.MqttMessageType=MqttMessageType;
    }

    public String getClientId() {
        return clientId;
    }

    public byte getQos() {
        return qos;
    }


    public byte[] getPayload() {
        return payload;
    }

    public String getUserName() {
        return userName;
    }

    public String getTopic() {
        return topic;
    }
    public MqttMessageType getMqttMessageType() {
        return MqttMessageType;
    }

}
