/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author 2dam
 */
public class ConnectionPool {
    
   private static BasicDataSource ds = null;
   private ResourceBundle configFile;
   private static String driverBD;
   private static String urlDB;
   private static String userBD;
   private List<Connection> pool;
   private static String passwordDB;
   private List <Connection> connectionPool;
    private List<Connection> usedConnections = new ArrayList<>();
    ConnectionPool connectionpool = new ConnectionPool(urlDB, userBD, passwordDB, pool);
    private static int INITIAL_POOL_SIZE = 10;
   
   
   public ConnectionPool(String urlDB1, String userBD1, String passwordDB1, List<Connection> pool) {
      this.configFile = ResourceBundle.getBundle("mkma.signupsignin.dataaccess.config");       
      this.driverBD = this.configFile.getString("Driver");
      this.urlDB = this.configFile.getString("Conn");
      this.userBD = this.configFile.getString("DBUser");
      this.passwordDB = this.configFile.getString("DBPass");
}
   
   public static DataSource getDataSource() {
       
       if (ds == null) {          
           ds = new BasicDataSource();
           ds.setDriverClassName(driverBD);
           ds.setUsername(userBD);
           ds.setPassword(passwordDB);
           ds.setUrl(urlDB);
           ds.setInitialSize(10);
           ds.setMaxIdle(3);
           ds.setMaxTotal(6);
           ds.setMaxWaitMillis(5000);    
       }
    return ds;
   }
   
 
   public  ConnectionPool create (String urlDB, String userBD, String passwordDB) throws SQLException {
 
        List<Connection> pool = new ArrayList<>(INITIAL_POOL_SIZE);
  
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            
                      pool.add((Connection) create(urlDB, userBD, passwordDB));           

        }
        
        
        
        return new ConnectionPool(urlDB, userBD, passwordDB, pool);
    }

   
    public Connection getConnection() {
        Connection connection = connectionPool
          .remove(connectionPool.size() - 1);
        usedConnections.add(connection);
        return connection;
    }
    
    public boolean releaseConnection(Connection connection) {
        connectionPool.add(connection);
        return usedConnections.remove(connection);
    }
    
    private static Connection createConnection(
      String url, String user, String password) 
      throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
    
    public int getSize() {
        return connectionPool.size() + usedConnections.size();
    }
   
   
   
}