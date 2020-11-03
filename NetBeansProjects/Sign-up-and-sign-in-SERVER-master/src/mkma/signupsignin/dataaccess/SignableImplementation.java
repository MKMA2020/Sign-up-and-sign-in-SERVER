/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import exceptions.DataBaseConnectionException;
import exceptions.PassNotCorrectException;
import exceptions.ServerErrorException;
import exceptions.UserNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import signable.Signable;
import user_message.User;

/**
 * This will be the server side SignableImplementation or Data Acces Object
 * (DAO). It will contain SignIn(User user), SignUp() and LogOut() methods.
 *
 * @author Martin Gros and Martin Valiente
 *
 */
public class SignableImplementation implements Signable {

    private final String checkUsername = "SELECT login from user where login = ?;";
    private final String checkPassword = "SELECT password from user where login = ? and password = ?;";
    private final String updateLastAccess = "UPDATE user set lastAcces=? where login = ?";
    private final String insertUser = "INSERT into user values (?,?,?,?,?,?,?,?,?)";
    private final String lastId = "SELECT count(id) from user;";

    /**
     * signIn Method mostly done. Exceptions yet to handle. Method will check if
     * a user and password combination exists in the database.
     *
     * @param user an User will be recieved.
     * @exception UserNotFoundException when the specified user doesn't exist in
     * the database.
     * @exception PassNotCorrectException when the specified password doesn't
     * match the user and the user exists.
     */
    @Override
    public User signIn(User user) throws DataBaseConnectionException, ServerErrorException, UserNotFoundException, PassNotCorrectException {

        try {
            // User and password declared and asigned values from recieved user for the select
            String username = user.getLogin();
            String password = user.getPassword();

            // Last Access of the User will be updated.
            Timestamp lastAccess = Timestamp.from(Instant.now());

            // Initialize objects and variables
            DaoConnection dao = new DaoConnection();
            ResultSet rs = null;

            try {
                // Start the connection.
                dao.conectar();
            } catch (Exception ex) {
                Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Create Statements
            PreparedStatement stmtUser = dao.con.prepareStatement(checkUsername);
            PreparedStatement stmtPass = dao.con.prepareStatement(checkPassword);

            // Set the Strings usernames and password to the final queries.
            stmtUser.setString(1, username);
            stmtPass.setString(1, username);
            stmtPass.setString(2, password);

            // Execute the User check query into rs Resultset and throw UserNotFoundException
            // if there is not a Username match
            rs = stmtUser.executeQuery();
            if (!rs.next()) {
                throw new UserNotFoundException();               
            }

            // Execute the Password check query into rs ResultSet and throw PassNotCorrectException
            // if there is not a match
            rs = stmtPass.executeQuery();
            if (!rs.next()) {
                throw new PassNotCorrectException();
            }

            
            // Update Last Access Query Code
            {
                // Create Statement
                PreparedStatement stmtLastAccess = dao.con.prepareStatement(updateLastAccess);
                // Set the lastAccess Timestamp into the final Query
                stmtLastAccess.setTimestamp(1, lastAccess);
                stmtLastAccess.setString(2, username);
                // Execute the Query              
                stmtLastAccess.executeUpdate();
            }
            try {
                dao.desconectar();
            } catch (Exception ex) {
                Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (SQLException ex ) {
            throw new DataBaseConnectionException();
        }
       
        
        return user;
    }

    /**
     * signUp Method mostly done. Exceptions yet to handle. Method will check if
     * the passed Username (Login) exists and will afterwards insert all its
     * data if it doesn't.
     *
     * @param user an User will be recieved.
     */
    @Override
    public User signUp(User user) {
        // User and password declared and asigned values from recieved user for the select
        
        String login = user.getLogin();
        String email = user.getEmail();
        String fullName = user.getFullName();
        String password = user.getPassword();
        Boolean isAdmin = false;
        Boolean isActive = true;
        Timestamp lastAccess = Timestamp.from(Instant.now());
        Timestamp lastPasswordChange = Timestamp.from(Instant.now());

        // Initialize objects and variables
        DaoConnection dao = new DaoConnection();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        long nId=findLastId();
        
        try {
            // Start the connection.
            dao.conectar();

            // Set the Strings username and password to the final query.
            stmt = dao.con.prepareStatement(insertUser);
            stmt.setLong(1, nId);
            stmt.setString(2, login);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setBoolean(5, isActive);
            stmt.setBoolean(6, isAdmin);
            stmt.setString(7, password);
            stmt.setTimestamp(8, lastAccess);
            stmt.setTimestamp(9, lastPasswordChange);

            try {
                // Execute the query and insert the user.
                stmt.executeUpdate();

            } catch (SQLException ex) {
                Logger.getLogger(SignableImplementation.class
                        .getName()).log(Level.SEVERE, null, ex);
                //Unable to add the new user.
            }
            dao.desconectar();

        } catch (Exception ex) {
            Logger.getLogger(SignableImplementation.class
                    .getName()).log(Level.SEVERE, null, ex);
            //Connection error
        }
        return user;
    }

    @Override
    public User signOut(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private long findLastId() {
        int id=0;
         try {
         // Initialize objects and variables
        DaoConnection dao = new DaoConnection();
        ResultSet rs = null;
        //starts the connection and gets de id.
        dao.conectar();
        PreparedStatement stmt = dao.con.prepareStatement(lastId);

        rs=stmt.executeQuery();
        if(rs.next())
            id=rs.getInt(1)+1;       
        //Ends the connection
        dao.desconectar();
      
        
         } catch (SQLException ex) {
            Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
         return id;
    }
}
