package io.moquette.interception.hazelcastHandler;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import io.moquette.BrokerConstants;
import io.moquette.interception.AbstractInterceptHandler;
import io.moquette.interception.messages.InterceptConnectMessage;
import io.moquette.interception.messages.InterceptConnectionLostMessage;
import io.moquette.interception.messages.InterceptPublishMessage;
import io.moquette.server.Server;


import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static io.netty.handler.codec.mqtt.MqttQoS.*;

public class HazelcastInterceptHandler extends AbstractInterceptHandler {
    private static final Logger LOG = LoggerFactory.getLogger("STDOUT");
    private final HazelcastInstance hz;
    private final Server server;
    private final static String _connect="onConnect";
    private final static String _disconnect="DisConnect";


    private static final byte defalutqos=(byte)0;
    public HazelcastInterceptHandler (Server server){
        this.hz = server.getHazelcastInstance();
        this.server=server;
    }
    @Override
    public String getID() {
        return null;
    }

    @Override
    public void onConnect(InterceptConnectMessage msg) {
        ITopic<HazelcastMsg> topic = hz.getTopic("moquette");
        ///   /brokers/<node>/clients/<clientid>/connected
        String topic_= BrokerConstants.publish_head_+BrokerConstants.cluster_name+BrokerConstants.Topic_split+msg.getClientID()+BrokerConstants.Device_sub_conncet;
        HazelcastMsg hazelcastMsg = new HazelcastMsg(msg.getClientID(), topic_, defalutqos,
            String.format(BrokerConstants.Device_pub_status_disconncet,msg.getClientID(),_connect).getBytes(), msg.getUsername(), MqttMessageType.CONNECT);
        topic.publish(hazelcastMsg);
        LOG.info("{} onConnect on {} message: {}", msg.getClientID(), topic_,  String.format(BrokerConstants.Device_pub_status_disconncet,msg.getClientID(),_disconnect));

        //MqttPublishMessage publishCONNECTMessage = MqttMessageBuilders.publish().topicName(topic_)
          //  .payload(Unpooled.copiedBuffer(ByteBuffer.wrap(String.format(BrokerConstants.Device_pub_status_disconncet,msg.getClientID(),_connect).getBytes())))
            //.qos(AT_LEAST_ONCE).build();

        //this.server.internalPublish(publishCONNECTMessage,msg.getClientID());

    }

    @Override
    public void onConnectionLost(InterceptConnectionLostMessage msg) {
        ITopic<HazelcastMsg> topic = hz.getTopic("moquette");
        String topic_= BrokerConstants.publish_head_+BrokerConstants.cluster_name+BrokerConstants.Topic_split+msg.getClientID()+BrokerConstants.Device_sub_disconncet;
        HazelcastMsg hazelcastMsg = new HazelcastMsg(msg.getClientID(), topic_, defalutqos,
            String.format(BrokerConstants.Device_pub_status_disconncet,msg.getClientID(),_disconnect).getBytes(), msg.getUsername(), MqttMessageType.DISCONNECT);
        topic.publish(hazelcastMsg);
        LOG.info("{} onConnectionLost on {} message: {}", msg.getClientID(), topic_,  String.format(BrokerConstants.Device_pub_status_disconncet,msg.getClientID(),_disconnect));

       // MqttPublishMessage publishCONNECTMessage = MqttMessageBuilders.publish().topicName(topic_)
         //   .payload(Unpooled.copiedBuffer(ByteBuffer.wrap(String.format(BrokerConstants.Device_pub_status_disconncet,msg.getClientID(),_disconnect).getBytes())))
         //   .qos(AT_LEAST_ONCE).build();

        //this.server.internalPublish(publishCONNECTMessage,msg.getClientID());

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
                bytes, msg.getUsername(), MqttMessageType.PUBLISH);
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
