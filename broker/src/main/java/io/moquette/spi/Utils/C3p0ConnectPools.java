package io.moquette.spi.Utils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class C3p0ConnectPools {
    private static ComboPooledDataSource cpds=null;
    public C3p0ConnectPools(String url,String username,String password,String drivername,int MaxPoolSize,int minPoolSize,int InitialPoolSize
    ,int MaxStatements,int MaxIdleTime) throws Exception{
            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(drivername);
            cpds.setJdbcUrl(url);
            cpds.setUser(username);
            cpds.setPassword(password);
            cpds.setMaxPoolSize(MaxPoolSize);
            cpds.setMinPoolSize(minPoolSize);
            cpds.setInitialPoolSize(InitialPoolSize);
            cpds.setMaxStatements(MaxStatements);
            cpds.setMaxIdleTime(MaxIdleTime);

    }
    public static Connection getConnection(){
        Connection conn = null;
        try {
            conn = cpds.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
