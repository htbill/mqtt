package io.moquette.interception.hazelcastHandler;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import io.moquette.server.ConnectionDescriptorStore;
import io.moquette.server.Server;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static io.netty.handler.codec.mqtt.MqttQoS.*;

public class HazelcastListener implements MessageListener<HazelcastMsg> {
    private static final Logger LOG = LoggerFactory.getLogger("STDOUT");

    private final Server server;
    private final ConnectionDescriptorStore connectionDescriptors_;

    public HazelcastListener(Server server, ConnectionDescriptorStore connectionDescriptors){
        this.server = server;
        this.connectionDescriptors_=connectionDescriptors;
    }
    @Override
    public void onMessage(Message<HazelcastMsg> msg) {
        try {
            //if (!msg.getPublishingMember().equals(server.getHazelcastInstance().getCluster().getLocalMember())) {}
            switch (msg.getMessageObject().getMqttMessageType()){
                case CONNECT:
                    LOG.info("{} received  connect from hazelcast for topic {} message: {}", msg.getMessageObject().getClientId(),
                        msg.getMessageObject().getTopic(), msg.getMessageObject().getPayload());
                    MqttPublishMessage publishCONNECTMessage = MqttMessageBuilders.publish().topicName(msg.getMessageObject().getTopic())
                        .payload(Unpooled.copiedBuffer(ByteBuffer.wrap(msg.getMessageObject().getPayload())))
                        .qos(Qos_enum(msg.getMessageObject().getQos())).build();

                    server.internalPublish(publishCONNECTMessage,msg.getMessageObject().getClientId());
                    break;
                case PUBLISH:
                    LOG.info("{} received public from hazelcast for topic {} message: {}", msg.getMessageObject().getClientId(),
                        msg.getMessageObject().getTopic(), msg.getMessageObject().getPayload());
                    MqttPublishMessage publishMessage = MqttMessageBuilders.publish().topicName(msg.getMessageObject().getTopic())
                        .payload(Unpooled.copiedBuffer(ByteBuffer.wrap(msg.getMessageObject().getPayload())))
                        .qos(Qos_enum(msg.getMessageObject().getQos())).build();
                    //publishMessage.setTopicName(msg.getMessageObject().getTopic());
                    //publishMessage.setQos(AbstractMessage.QOSType.valueOf(msg.getMessageObject().getQos()));
                    //publishMessage.setPayload(ByteBuffer.wrap(msg.getMessageObject().getPayload()));
                    //publishMessage.setLocal(false);
                    //publishMessage.setClientId(msg.getMessageObject().getClientId());
                    server.internalPublish(publishMessage,msg.getMessageObject().getClientId());
                    break;
                case DISCONNECT:
                    LOG.info("{} received  disconnect from hazelcast for topic {} message: {}", msg.getMessageObject().getClientId(),
                        msg.getMessageObject().getTopic(), msg.getMessageObject().getPayload());
                    /*if (connectionDescriptors_.isConnected(msg.getMessageObject().getClientId())){
                        if (connectionDescriptors_.lookupDescriptor(msg.getMessageObject().getClientId()).isPresent()){
                            connectionDescriptors_.lookupDescriptor(msg.getMessageObject().getClientId()).get().abort();
                        }
                    }*/
                    MqttPublishMessage publishDISCONNECTMessage = MqttMessageBuilders.publish().topicName(msg.getMessageObject().getTopic())
                        .payload(Unpooled.copiedBuffer(ByteBuffer.wrap(msg.getMessageObject().getPayload())))
                        .qos(Qos_enum(msg.getMessageObject().getQos())).build();
                    server.internalPublish(publishDISCONNECTMessage,msg.getMessageObject().getClientId());
                    break;
                    default:
                        break;
            }

            //}
        } catch (Exception ex) {
            LOG.error("error polling hazelcast msg queue", ex);
        }
    }
    public static MqttQoS Qos_enum(byte qos){
        switch(qos) {
            case 0x00:
                return AT_MOST_ONCE;
            case 0x01:
                return AT_LEAST_ONCE;
            case 0x02:
                return EXACTLY_ONCE;
            case (byte) 0x80:
                return FAILURE;
            default:
                throw new IllegalArgumentException("Invalid QOS Type. Expected either 0, 1, 2, or 0x80. Given: " + qos);
        }

    }
}
