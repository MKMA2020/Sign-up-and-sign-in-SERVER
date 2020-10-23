/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.dataaccess;

import exceptions.PassNotCorrectException;
import exceptions.UserNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import signable.Signable;
import user_message.Message;
import user_message.User;

/**
 * This will be the server side SignableImplementation or Data Acces Object
 * (DAO). It will contain SignIn(User user), SignUp() and LogOut() methods.
 *
 * @author Martin Gros and Martin Valiente
 * 
 */
public class SignableImplementation implements Signable {

    private final String checkUsername = "SELECT username from mkmadb.users where username is ?;";
    private final String checkPassword = "SELECT password from mkmadb.users where username is ? and password is ?;";
    private final String updateLastAccess = "UPDATE users set lastAccess=? where login is ?";
    private final String insertUser = "INSERT into users values (?,?,?,?,?,?,?)";

    /**
     * signIn Method mostly done. Exceptions yet to handle. Method will check if
     * a user and password combination exists in the database.
     * 
     * @param user an User will be recieved.
     * @exception UserNotFoundException when the specified user doesn't exist in the database.
     * @exception PassNotCorrectException when the specified password doesn't match the user and the user exists.
     */
    @Override
    public void signIn(Message message) {
        
        // User and password declared and asigned values from recieved user for the select
        String username = message.getUser().getLogin();
        String password = message.getUser().getPassword();
        // Last Acces of the User will be updated.
        Timestamp lastAccess = Timestamp.from(Instant.now());
        boolean logInSuccess = false;

        // Initialize objects and variables
        DaoConnection dao = new DaoConnection();
        ResultSet rs = null;
        try {
            // Start the connection.
            dao.conectar();

            // Set the Strings username and password to the final queries.
            dao.con.prepareStatement(checkUsername).setString(1, username);
            dao.con.prepareStatement(checkPassword).setString(1, username);
            dao.con.prepareStatement(checkPassword).setString(2, password);

            try {
                rs = dao.con.prepareStatement(checkUsername).executeQuery();
                if (rs.next()) {
                    try {
                        rs = dao.con.prepareStatement(checkPassword).executeQuery();
                        if (rs.next()) {
                            logInSuccess = true;
                            try {
                                dao.con.prepareStatement(updateLastAccess);
                            } catch (SQLException ex) {
                                Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
                                // FALLO AL ACTUALIZAR LA FECHA
                            }
                        }
                    } catch (PassNotCorrectException  PassNotCorrect) {
                        Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, PassNotCorrect);
                        //USUARIO SI EXISTE PERO LA CONTRASEÑA ES INCORRECTA
                        message.setMessageType(Message.MessageType.PassNotCorrect.toString());
                    }

                }
            } catch (UserNotFoundException UserNotFound) {
                Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, UserNotFound);
                // EL NOMBRE DE USUARIO NO EXISTE O ES INCORRECTO
            }
            dao.desconectar();

        } catch (Exception ex) {
            Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
            // FALLA LA CONEXION
        }

    }

    /**
     * signUp Method mostly done. Exceptions yet to handle. Method will check if
     * the passed Username (Login) exists and will afterwards insert all its
     * data if it doesn't.
     *
     * @param user an User will be recieved.
     */
    @Override
    public void signUp(Message message) {
        // User and password declared and asigned values from recieved user for the select
        long id = message.getUser().getId();
        String login = message.getUser().getLogin();
        String email = message.getUser().getEmail();
        String fullName = message.getUser().getFullName();
        String password = message.getUser().getPassword();
        Timestamp lastAccess = Timestamp.from(Instant.now());
        Timestamp lastPasswordChange = Timestamp.from(Instant.now());

        boolean logInSuccess = false;

        // Initialize objects and variables
        DaoConnection dao = new DaoConnection();
        ResultSet rs = null;
        try {
            // Start the connection.
            dao.conectar();

            // Set the Strings username and password to the final query.
            dao.con.prepareStatement(insertUser).setLong(1, id);
            dao.con.prepareStatement(insertUser).setString(2, login);
            dao.con.prepareStatement(insertUser).setString(3, email);
            dao.con.prepareStatement(insertUser).setString(4, fullName);
            dao.con.prepareStatement(insertUser).setString(5, password);
            dao.con.prepareStatement(insertUser).setTimestamp(6, lastAccess);
            dao.con.prepareStatement(insertUser).setTimestamp(7, lastPasswordChange);

            try {
                // Execute the query and insert the user.
                dao.con.prepareStatement(insertUser).executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
                //Unable to add the new user.
            }
            dao.desconectar();
        } catch (Exception ex) {
            Logger.getLogger(SignableImplementation.class.getName()).log(Level.SEVERE, null, ex);
            //COnnection error
        }

    }

    @Override
    public void signOut(Message message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
