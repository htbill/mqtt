/*
 * Copyright (c) 2012-2018 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.moquette.spi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.moquette.connections.IConnectionsManager;
import io.moquette.server.netty.NettyUtils;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.Utils.DataStatistics;
import io.moquette.spi.Utils.kafkaProducerMsg;
import io.moquette.spi.Utils.kafkabean;
import io.moquette.spi.impl.subscriptions.Topic;
import io.moquette.spi.security.IAuthorizatorPolicy;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

import static io.moquette.spi.impl.ProtocolProcessor.asStoredMessage;

class Qos0PublishHandler extends QosPublishHandler {

    private static final Logger LOG = LoggerFactory.getLogger(Qos0PublishHandler.class);

    private final IMessagesStore m_messagesStore;
    private final BrokerInterceptor m_interceptor;
    private final MessagesPublisher publisher;

    public Qos0PublishHandler(IAuthorizatorPolicy authorizator, IMessagesStore messagesStore, BrokerInterceptor interceptor,
                              MessagesPublisher messagesPublisher) {
        super(authorizator);
        this.m_messagesStore = messagesStore;
        this.m_interceptor = interceptor;
        this.publisher = messagesPublisher;

    }

    void receivedPublishQos0(Channel channel, MqttPublishMessage msg) throws UnsupportedEncodingException {
        // verify if topic can be write
        final Topic topic = new Topic(msg.variableHeader().topicName());
        if (topic.toString().startsWith(DataStatistics.PassThrough,0)){
            String clientID = NettyUtils.clientID(channel);
            String username = NettyUtils.userName(channel);
            kafkabean kafkabean_=new kafkabean();
            kafkabean_.clientid=clientID;
            kafkabean_.timemap=(System.currentTimeMillis());
            kafkabean_.username=(username);
            byte[] req = new byte[msg.payload().readableBytes()];
            msg.payload().readBytes(req);
            kafkabean_.data=(new String(req,"UTF-8"));
            kafkaProducerMsg.SendMessage(JSONObject.toJSONString(kafkabean_));
        }else {
            String clientID = NettyUtils.clientID(channel);
            String username = NettyUtils.userName(channel);
            if (!NettyUtils.ClientACLAuth(channel,topic.toString())){
                if (!m_authorizator.canWrite(topic, username, clientID)) {
                    LOG.error("MQTT client is not authorized to publish on topic. CId={}, topic={}", clientID, topic);
                    return;
                }
                NettyUtils.ClientACLAuth(channel,topic.toString(),true);
            }
            // route message to subscribers
            IMessagesStore.StoredMessage toStoreMsg = asStoredMessage(msg);
            toStoreMsg.setClientID(clientID);
            //进行迭代二叉树进行传输


            this.publisher.publish2Subscribers(toStoreMsg, topic);

            if (msg.fixedHeader().isRetain()) {
                // QoS == 0 && retain => clean old retained
                m_messagesStore.cleanRetained(topic);
            }
            //同步数据
            m_interceptor.notifyTopicPublished(msg, clientID, username);
        }

    }
}
