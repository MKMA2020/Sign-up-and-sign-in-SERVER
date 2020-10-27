/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import java.sql.Connection;
import java.sql.SQLException;
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
   private static String passwordDB;
   
   public ConnectionPool() {
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
   
   public static Connection getConnection() throws SQLException {
       return getDataSource().getConnection();
   }
}
