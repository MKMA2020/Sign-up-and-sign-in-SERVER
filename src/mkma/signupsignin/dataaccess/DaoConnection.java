/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This will be the temporary Dao Connection Class until the pool is implemented.
 * @author Martin Gros and Martin Valiente
 */
public class DaoConnection {
    // Attributes

    Connection con = null;
    private Properties props;
    private Statement stmt = null;

    // The next attributes are used to gather values of the configuration file
    private ResourceBundle configFile;
    private String driverBD;
    private String urlDB;
    private String userBD;
    private String passwordDB;

    /**
     * Gets the configuration from the control package.
     */
    public DaoConnection() {

        // We will use the file config (control package) for the connection
        this.configFile = ResourceBundle.getBundle("mkma.signupsignin.dataaccess.config");
        this.driverBD = this.configFile.getString("Driver");
        this.urlDB = this.configFile.getString("Conn");
        this.userBD = this.configFile.getString("DBUser");
        this.passwordDB = this.configFile.getString("DBPass");
    }

    /**
     * Connects to the database with the login information taken from the
     * control package (Information gathered previously).
     *
     * @throws Exception
     */
    protected void conectar() throws Exception {
        try {
            Class.forName(this.driverBD);
            con = DriverManager.getConnection(this.urlDB, this.userBD, this.passwordDB);
            stmt = con.createStatement();
        } catch (SQLException e) {
            throw new Exception("Error de SQL al conectar " + e.getMessage());
        }
    }

    /**
     * Disconnects from the database.
     *
     * @throws Exception
     */
    protected void desconectar() throws Exception {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            throw new Exception("Error de SQL al desconectar " + e.getMessage());
        }
    }
}

