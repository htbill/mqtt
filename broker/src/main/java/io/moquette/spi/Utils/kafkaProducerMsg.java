package io.moquette.spi.Utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
public class kafkaProducerMsg {
    private static KafkaProducer<String, String> producer=null;

    public  static String TOPIC = "test5";
    public kafkaProducerMsg(String topic,String Servers){
        Properties props = new Properties();
        TOPIC=topic;
        props.put("bootstrap.servers", Servers);//xxx服务器ip
        props.put("acks", "1");//所有follower都响应了才认为消息提交成功，即"committed"
        props.put("retries", 0);//retries = MAX 无限重试，直到你意识到出现了问题:)
        props.put("batch.size", 163840);//producer将试图批处理消息记录，以减少请求次数.默认的批量处理消息字节数
        //batch.size当批量的数据大小达到设定值后，就会立即发送，不顾下面的linger.ms
        props.put("linger.ms", 5);//延迟1ms发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录，producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
        props.put("buffer.memory", 33554432);//producer可以用来缓存数据的内存大小。
        props.put("key.serializer",
            "org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer",
            "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String, String>(props);
    }
    public static void SendMessage(String data){
        producer.send(new ProducerRecord<String, String>(TOPIC, data));
    }
}
