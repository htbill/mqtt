package io.moquette.spi.impl.security;

import io.moquette.BrokerConstants;
import io.moquette.server.config.IConfig;
import io.moquette.spi.Utils.C3p0ConnectPools;
import io.moquette.spi.impl.subscriptions.Topic;
import io.moquette.spi.security.IAuthorizatorPolicy;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ACLDBAuth implements IAuthorizatorPolicy {
    private static final Logger LOG = LoggerFactory.getLogger(ACLDBAuth.class);
    //DB_AUTHACL_QUERY
    static String readsqlQUERY;
    static String writesqlQUERY;
    public ACLDBAuth(IConfig conf) throws NoSuchAlgorithmException {
        this.readsqlQUERY=conf.getProperty(BrokerConstants.DB_AUTHACL_Read_QUERY, "");
        this.writesqlQUERY=conf.getProperty(BrokerConstants.DB_AUTHACL_Write_QUERY, "");

    }
    @Override
    public boolean canWrite(Topic topic, String user, String client) {
        if (topic == null || client == null) {
            LOG.info("username or password was null");
            return false;
        }

        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection conn = null;
        try {
            conn=C3p0ConnectPools.getConnection();
            preparedStatement = conn.prepareStatement(this.writesqlQUERY);
            //String encodedPasswd = new String(Hex.encodeHex(digest));
            preparedStatement.setString(1, client);
            //preparedStatement.setString(2, user);
            preparedStatement.setString(2, topic.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return true;
            }
        }catch (Exception e){
            LOG.info("IAuthenticator mode is error  "+e.getMessage());

        }finally {
            if (resultSet!=null){
                try {
                    resultSet.close();
                }catch (Exception e){

                }

            }
            if (preparedStatement!=null){
                try {
                    preparedStatement.close();
                }catch (Exception e){

                }
            }
            if (conn!=null){
                try {
                    conn.close();
                }catch (Exception e){

                }

            }

        }
        return false;
    }

    @Override
    public boolean canRead(Topic topic, String user, String client) {
        if (topic == null || client == null) {
            LOG.info("username or password was null");
            return false;
        }
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection conn = null;
        try {
            conn=C3p0ConnectPools.getConnection();
            preparedStatement = conn.prepareStatement(this.readsqlQUERY);
            //String encodedPasswd = new String(Hex.encodeHex(digest));
            preparedStatement.setString(1, client);
            //preparedStatement.setString(2, user);
            preparedStatement.setString(2, topic.toString());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {

                return true;
            }
        }catch (Exception e){
            LOG.info("IAuthenticator mode is error  "+e.getMessage());

        }finally {
            if (resultSet!=null){
                try {
                    resultSet.close();
                }catch (Exception e){

                }

            }
            if (preparedStatement!=null){
                try {
                    preparedStatement.close();
                }catch (Exception e){

                }
            }
            if (conn!=null){
                try {
                    conn.close();
                }catch (Exception e){

                }

            }

        }
        return false;
    }
}
