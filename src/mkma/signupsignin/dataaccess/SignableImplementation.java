package mkma.signupsignin.dataaccess;

import exceptions.DataBaseConnectionException;
import exceptions.PassNotCorrectException;
import exceptions.ServerErrorException;
import exceptions.UserExistsException;
import exceptions.UserNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
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
    private final String checkPasswordAndReturnUser = "SELECT * from user where login = ? and password = ?;";
    private final String updateLastAccess = "UPDATE user set lastAcces=? where login = ?";
    private final String insertUser = "INSERT into user values (?,?,?,?,?,?,?,?,?)";
    private final String lastId = "SELECT count(id) from user;";

    /**
     * Method will check if the user and password exist in the database.
     *
     * @param user The user that will be received.
     * @return The user that will be returned.
     * @throws DataBaseConnectionException When there is a problem in the
     * database connection.
     * @throws ServerErrorException When there is a server error.
     * @throws UserNotFoundException When the user doesn't exist.
     * @throws PassNotCorrectException When the password is incorrect.
     */
    @Override
    public User signIn(User user) throws DataBaseConnectionException, ServerErrorException, UserNotFoundException, PassNotCorrectException {
        Connection con = null;
        PreparedStatement stmtUser = null;
        PreparedStatement stmtPass = null;
        ResultSet rs = null;
        try {
            // User and password declared and asigned values from recieved user for the select
            String username = user.getLogin();
            String password = user.getPassword();

            // Last Access of the User will be updated.
            Timestamp lastAccess = Timestamp.from(Instant.now());

            // Start the connection.
            con = ConnectionPool.getConnection();

            // Create Statements
            stmtUser = con.prepareStatement(checkUsername);
            stmtPass = con.prepareStatement(checkPasswordAndReturnUser);

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
            } else {
                user.setId(rs.getLong(1));
                user.setLogin(rs.getString(2));
                user.setEmail(rs.getString(3));
                user.setFullName(rs.getString(4));
            }

            // Update Last Access Query Code
            // Create Statement
            PreparedStatement stmtLastAccess = con.prepareStatement(updateLastAccess);
            // Set the lastAccess Timestamp into the final Query
            stmtLastAccess.setTimestamp(1, lastAccess);
            stmtLastAccess.setString(2, username);
            // Execute the Query              
            stmtLastAccess.executeUpdate();

        } catch (SQLException ex) {
            throw new DataBaseConnectionException();
        } finally {
            try {
                rs.close();
                stmtUser.close();
                stmtPass.close();
                con.close();
            } catch (SQLException ex) {
                throw new DataBaseConnectionException();
            }
        }

        return user;
    }

    /**
     * Method will check if the passed Username (Login) exists and will
     * afterwards insert all its data if it doesn't.
     *
     * @param user The user that will be received.
     * @return The user that will be returned.
     * @throws UserExistsException When the user exists in the database.
     * @throws DataBaseConnectionException When there is a problem in the
     * database connection.
     * @throws ServerErrorException When there is a server error.
     */
    @Override
    public User signUp(User user) throws UserExistsException, DataBaseConnectionException, ServerErrorException {
        // Initialize objects and variables
        Connection con = null;
        PreparedStatement stmt = null;
        try {

            // User and password declared and asigned values from recieved user for the select
            String login = user.getLogin();
            String email = user.getEmail();
            String fullName = user.getFullName();
            String password = user.getPassword();
            Boolean isAdmin = false;
            Boolean isActive = true;
            Timestamp lastAccess = Timestamp.from(Instant.now());
            Timestamp lastPasswordChange = Timestamp.from(Instant.now());

            long nId = findLastId();

            // Start the connection.
            con = ConnectionPool.getConnection();

            // Set the Strings username and password to the final query.
            stmt = con.prepareStatement(insertUser);
            stmt.setLong(1, nId);
            stmt.setString(2, login);
            stmt.setString(3, email);
            stmt.setString(4, fullName);
            stmt.setBoolean(5, isActive);
            stmt.setBoolean(6, isAdmin);
            stmt.setString(7, password);
            stmt.setTimestamp(8, lastAccess);
            stmt.setTimestamp(9, lastPasswordChange);

            // Execute the query and insert the user.
            try {
                stmt.executeUpdate();
            } catch (SQLException ex) {
                throw new UserExistsException();
            }

        } catch (SQLException ex) {
            throw new DataBaseConnectionException();
        } finally {
            try {
                stmt.close();
                con.close();
            } catch (SQLException ex) {
                throw new DataBaseConnectionException();
            }
        }
        return user;
    }

    @Override
    public User signOut(User user) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method will return the last id of the user table.
     *
     * @return id as a long.
     * @throws SQLException When there is a problem related to SQL.
     * @throws ServerErrorException When there is a problem
     */
    private long findLastId() throws SQLException, ServerErrorException {
        // Initialize objects and variables
        int id = 0;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {

            //starts the connection and gets the id.
            con = ConnectionPool.getConnection();
            stmt = con.prepareStatement(lastId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1) + 1;
            }
            //Ends the connection

        } catch (SQLException ex) {
            throw new ServerErrorException();
        } catch (Exception ex) {
            throw new DataBaseConnectionException();
        } finally {
            try {
                rs.close();
                stmt.close();
                con.close();
            } catch (SQLException ex) {
                throw new DataBaseConnectionException();
            }
        }
        return id;
    }
}
