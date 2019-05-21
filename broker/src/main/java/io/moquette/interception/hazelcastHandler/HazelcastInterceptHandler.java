package io.moquette.interception.hazelcastHandler;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.mqtt.MqttQoS.*;

public class HazelcastInterceptHandler extends AbstractInterceptHandler {
    private static final Logger LOG = LoggerFactory.getLogger(HazelcastInterceptHandler.class);
    private final HazelcastInstance hz;

    public HazelcastInterceptHandler (Server server){
        this.hz = server.getHazelcastInstance();
    }
    @Override
    public String getID() {
        return null;
    }

    @Override
    public void onPublish(InterceptPublishMessage msg) {
        try {
            byte[] bytes = new byte[msg.getPayload().readableBytes()];
            msg.getPayload().getBytes(msg.getPayload().readerIndex(), bytes);
            String str = new String(bytes, 0, msg.getPayload().readableBytes());
            //byte[]t=msg.getPayload().array();
            LOG.info("{} publish on {} message: {}", msg.getClientID(), msg.getTopicName(), str);
            ITopic<HazelcastMsg> topic = hz.getTopic("moquette");
            HazelcastMsg hazelcastMsg = new HazelcastMsg(msg.getClientID(), msg.getTopicName(), byteValue(msg.getQos()),
                bytes, msg.getUsername());
            topic.publish(hazelcastMsg);
        }catch (Exception e){
            LOG.info(e.getMessage());
        }

    }
    public byte byteValue(MqttQoS MqttQoS_) {
        switch(MqttQoS_) {
            case AT_MOST_ONCE:
                return 0;
            case AT_LEAST_ONCE:
                return 1;
            case EXACTLY_ONCE:
                return 2;
            case FAILURE:
                return (byte) 0x80;
            default:
                throw new IllegalArgumentException("Cannot give byteValue of QOSType: " );
        }
    }
}
