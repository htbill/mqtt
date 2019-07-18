package io.moquette.spi.Utils;

import io.moquette.BrokerConstants;

import java.io.Serializable;

public class kafkabean implements Serializable,Cloneable{
    private static kafkabean ekafkabean = new kafkabean();
    private kafkabean(){
        super();
    }

    public String clientid;
    public String  data;
    public long timemap;
    public String username;
    public String tostrings(){
        return String.format(BrokerConstants.Device_pub_detail_msg,clientid,data,timemap,username);
    }
    /**
     * 调用对象创建优化
     *
     * @return
     */
    public static kafkabean getInstance(String clientid,String  data,long timemap,String username){
        try {
             kafkabean lkafkabean=(kafkabean) ekafkabean.clone();
            lkafkabean.clientid=clientid;
            lkafkabean.data=data;
            lkafkabean.timemap=timemap;
            lkafkabean.username=username;
             return lkafkabean;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new kafkabean();
    }


}
