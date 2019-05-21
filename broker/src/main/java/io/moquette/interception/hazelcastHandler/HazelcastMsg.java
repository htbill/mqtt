package io.moquette.interception.hazelcastHandler;

import java.io.Serializable;

public class HazelcastMsg implements Serializable {
    private final String clientId;
    private final String userName;
    private final byte qos;
    private final byte[] payload;
    private final String topic;

    public HazelcastMsg(String clientId, String topic, byte qos, byte[] payload, String userName) {
        this.clientId = clientId;
        this.qos = qos;
        this.payload = payload;
        this.userName = userName;
        this.topic = topic;
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
}
