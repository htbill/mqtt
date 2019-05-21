package io.moquette.spi.impl.security;

import io.moquette.BrokerConstants;
import io.moquette.server.config.IConfig;
import io.moquette.spi.Utils.C3p0ConnectPools;
import io.moquette.spi.security.IAuthenticator;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthDbConnectPool implements IAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(AuthDbConnectPool.class);
   // static C3p0ConnectPools C3p0ConnectPools_;
    static String sqlQUERY=null;
    private static MessageDigest messageDigest = null;
    public AuthDbConnectPool(String driver, String jdbcUrl, String sqlUsername,String sqlPassword,int MaxPoolSize,int minPoolSize,int InitialPoolSize
        ,int MaxStatements,int MaxIdleTime){
        try {
            //C3p0ConnectPools_=new C3p0ConnectPools(jdbcUrl,sqlUsername,sqlPassword,driver,MaxPoolSize,minPoolSize,InitialPoolSize,MaxStatements,MaxIdleTime);
        }catch (Exception e){

        }
    }
    public AuthDbConnectPool(IConfig conf) throws NoSuchAlgorithmException {
        //this(conf.getProperty(BrokerConstants.DB_AUTHENTICATOR_DRIVER, ""),
          //  conf.getProperty(BrokerConstants.DB_AUTHENTICATOR_URL, ""),
            //conf.getProperty(BrokerConstants.DB_Auth_sqlUsername, ""),
            //conf.getProperty(BrokerConstants.DB_Auth_sqlpassword, ""),
            //Integer.parseInt(conf.getProperty(BrokerConstants.DB_Auth_MaxPoolSize, "")),
            //Integer.parseInt(conf.getProperty(BrokerConstants.DB_Auth_minPoolSize, "")),
            //Integer.parseInt(conf.getProperty(BrokerConstants.DB_Auth_InitialPoolSize, "")),
            //Integer.parseInt(conf.getProperty(BrokerConstants.DB_Auth_MaxStatements, "")),
            //Integer.parseInt(conf.getProperty(BrokerConstants.DB_Auth_MaxIdleTime, "")));
        this.sqlQUERY=conf.getProperty(BrokerConstants.DB_AUTHENTICATOR_QUERY, "");
        this.messageDigest = MessageDigest.getInstance(conf.getProperty(BrokerConstants.DB_AUTHENTICATOR_DIGEST, ""));
    }
    @Override
    public boolean checkValid(String clientId, String username, byte[] password) {
        if (username == null || password == null) {
            LOG.info("username or password was null");
            return false;
        }
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection conn = null;
        try {
            conn=C3p0ConnectPools.getConnection();
            preparedStatement = conn.prepareStatement(this.sqlQUERY);
            //String encodedPasswd = new String(Hex.encodeHex(digest));
            preparedStatement.setString(1, username);
            messageDigest.update(password);
            byte[] digest = messageDigest.digest();
            String encodedPasswd = new String(Hex.encodeHex(digest));
            preparedStatement.setString(2, encodedPasswd);
            preparedStatement.setString(3, clientId);
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
