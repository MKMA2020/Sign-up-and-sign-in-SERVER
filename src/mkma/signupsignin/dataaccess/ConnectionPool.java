package mkma.signupsignin.dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * This class will create and manage the Database Connection Pool.
 *
 * @author Martin Valiente Ainz
 */
public class ConnectionPool {

    static ResourceBundle configFile;
    String viewOption;
    String modelOption;

    private static BasicDataSource ds = new BasicDataSource();

    static {
        configFile = ResourceBundle.getBundle("mkma.signupsignin.dataaccess.config");
        ds.setUrl(configFile.getString("ServerIP"));
        ds.setUsername(configFile.getString("User"));
        ds.setPassword(configFile.getString("Password"));
        ds.setMaxTotal(Integer.parseInt(configFile.getString("MaxConnections")));
        ds.setMaxOpenPreparedStatements(Integer.parseInt(configFile.getString("MaxOpenStatements")));
    }

    /**
     * Method will return a connection from the pool.
     *
     * @return A connection from the pool.
     * @throws SQLException When a SQL Exception Occurs.
     */
    public static synchronized Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
