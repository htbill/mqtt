package io.moquette.spi.impl.BrokerMetrics;

import java.sql.Connection;
import java.sql.SQLException;

public interface Metricshandle {
    void BrokerActiveCOUNT(String BrokerName, long ActiverCount,long ToltalOnline,long TotalUnline, Connection sqlConnection) throws SQLException;
    void BrokerPublishSubMsgSize(String BrokerName, long PublishMsgSize, long SubMsgSize,Connection sqlConnection) throws SQLException;


}
