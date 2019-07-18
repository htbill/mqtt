package io.moquette.spi.impl.BrokerMetrics;

import io.moquette.server.Server;
import io.moquette.server.netty.NettyAcceptor;
import org.apache.kafka.common.protocol.types.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Metriciml implements Metricshandle{
    private static final Logger LOG = LoggerFactory.getLogger(Metriciml.class);
    static String ActiveCOUNTsql="insert into mqtt_metric_connect(brokername,ActiverCount,ToltalOnline,TotalUnline) values(\"%s\",%s,%s,%s)";
    static String PublishSubMsgSizesql="insert into mqtt_metric_pub_sub(brokername,PublishMsgSize,SubMsgSize) values(\"%s\",%s,%s)";
    Statement stmt = null;
    @Override
    public void BrokerActiveCOUNT(String BrokerName, long ActiverCount, long ToltalOnline, long TotalUnline, Connection sqlConnection) throws SQLException {
        if (sqlConnection!=null){
            try {
                stmt=sqlConnection.createStatement();
                stmt.execute(String.format(ActiveCOUNTsql,BrokerName,ActiverCount,ToltalOnline,TotalUnline));
            }catch (Exception e){
                LOG.info("MysqlServer Metriciml is Exception={}", e.getMessage());

            }finally {
                if (stmt!=null){
                    stmt.close();

                }
                if (sqlConnection!=null){
                    sqlConnection.close();
                }
            }



        }
    }

    @Override
    public void BrokerPublishSubMsgSize(String BrokerName, long PublishMsgSize, long SubMsgSize, Connection sqlConnection) throws SQLException {
        if (sqlConnection!=null) {
            try {
                stmt = sqlConnection.createStatement();
                stmt.execute(String.format(PublishSubMsgSizesql, BrokerName, PublishMsgSize, SubMsgSize));
            } catch (Exception e) {
                LOG.info("MysqlServer Metriciml is Exception={}", e.getMessage());

            } finally {
                if (stmt != null) {
                    stmt.close();

                }
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            }
        }}

}
