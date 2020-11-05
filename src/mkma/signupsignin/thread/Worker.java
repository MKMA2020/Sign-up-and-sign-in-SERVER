package mkma.signupsignin.thread;

import exceptions.DataBaseConnectionException;
import exceptions.PassNotCorrectException;
import exceptions.ServerErrorException;
import exceptions.TimeOutException;
import exceptions.UserExistsException;
import exceptions.UserNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mkma.signupsignin.application.Application;
import mkma.signupsignin.dataaccess.SignableFactory;
import signable.Signable;
import user_message.Message;
import user_message.MessageType;
import user_message.User;

/**
 * This class is the server thread. It gets the message, sends it to the
 * database and then sends it back with the changes needed.
 *
 * @author Kerman Rodríguez
 */
public class Worker extends Thread {

    //Variable declaration
    private Socket service;
    private Message returnMessage;
    private User user;
    private Message received;
    private Boolean availableConnection;

    //Thread constructor
    public Worker(Socket service, Boolean availableConnections) {
        this.service = service;
        this.availableConnection = availableConnections;
    }

    /**
     * This method starts the thread when the server socket creates a socket,
     * and treats the client's request.
     */
    @Override
    public void run() {
        returnMessage = new Message(null, null);
        //Checks if any more clients can get in
        if (availableConnection) {
            Application.getConnection();
            ObjectInputStream entry = null;
            InputStream input = null;

            //Opening of the entry stream
            try {
                input = service.getInputStream();
                entry = new ObjectInputStream(input);
            } catch (IOException e) {
                System.out.println(e);
            }

            //Message received and treated with exception catching
            try {
                received = (Message) entry.readObject();
                SignableFactory factory = new SignableFactory();
                Signable signable = factory.getSignable();

                switch (received.getMessageType()) {
                    case SIGNIN:
                        user = signable.signIn(received.getUser());
                        returnMessage.setUser(user);
                        returnMessage.setMessageType(MessageType.OKAY);
                        break;
                    case SIGNUP:
                        user = signable.signUp(received.getUser());
                        returnMessage.setUser(user);
                        returnMessage.setMessageType(MessageType.OKAY);
                        break;
                }

            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
                //Closes the streams and the socket
            } catch (DataBaseConnectionException ex) {
                returnMessage.setMessageType(MessageType.DATABASEERROR);
            } catch (PassNotCorrectException ex) {
                returnMessage.setMessageType(MessageType.PASSNOTCORRECT);
            } catch (ServerErrorException ex) {
                returnMessage.setMessageType(MessageType.SERVERERROR);
            } catch (TimeOutException ex) {
                returnMessage.setMessageType(MessageType.TIMEOUTEXCEPTION);
            } catch (UserNotFoundException ex) {
                returnMessage.setMessageType(MessageType.USERNOTFOUND);
            } catch (UserExistsException ex) {
                returnMessage.setMessageType(MessageType.USEREXISTS);
                //It sends the message back and then closes the socket
            } finally {
                if (returnMessage.getMessageType() != MessageType.OKAY) {
                    returnMessage.setUser(null);
                }
                returnMessage(returnMessage, service);
                try {
                    entry.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                try {
                    input.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                try {
                    service.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                Application.releaseConnection();
            }
            //If there are no available connections, it returns an error to the 
            //client and closes the socket.
        } else {
            returnMessage.setUser(null);
            returnMessage.setMessageType(MessageType.SERVERERROR);
            returnMessage(returnMessage, service);
            try {
                service.close();
            } catch (IOException ex) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * This method will send the modified message back to the client to be
     * treated.
     *
     * @param message the message to send back to the client
     * @param socket the socket used to receive and send the message back
     */
    public synchronized void returnMessage(Message message, Socket socket) {
        //Opens an output stream
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;

        //Defines the object and the stream, and sends a message
        try {
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            //Closes the socket and the stream
        } finally {
            try {
                objectOutputStream.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            try {
                outputStream.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

}
