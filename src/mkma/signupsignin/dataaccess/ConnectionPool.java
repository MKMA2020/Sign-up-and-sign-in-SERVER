/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author Martin Valiente Ainz
 */
public class ConnectionPool {

    static ResourceBundle configFile;

    private static BasicDataSource ds = new BasicDataSource();

    static {
        configFile = ResourceBundle.getBundle("mkma.signupsignin.dataaccess.config");
        ds.setUrl(configFile.getString("ServerIP"));
        ds.setUsername(configFile.getString("User"));
        ds.setPassword(configFile.getString("Password"));
        ds.setMaxTotal(Integer.parseInt(configFile.getString("MaxConnections")));
        ds.setMaxOpenPreparedStatements(Integer.parseInt(configFile.getString("MaxOpenStatements")));
    }

    public static synchronized Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
