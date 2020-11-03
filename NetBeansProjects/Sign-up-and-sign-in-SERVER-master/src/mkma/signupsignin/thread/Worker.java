/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import mkma.signupsignin.dataaccess.SignableFactory;
import signable.Signable;
import user_message.Message;
import user_message.MessageType;
import user_message.User;

/**
 *
 * @author 2dam
 */
public class Worker extends Thread {

    private Socket service;
    private Message returnMessage;
    private User user;
    private Message received;

    public Worker(Socket service) {
        this.service = service;
    }

    @Override
    public void run() {

        ObjectInputStream entry = null;
        InputStream input = null;
        returnMessage = new Message(null, null);

        //Opening of the entry stream
        try {
            input = service.getInputStream();
            entry = new ObjectInputStream(input);
        } catch (IOException e) {
            System.out.println(e);
        }

        //Message received and treated
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
            
        } finally {
            if (returnMessage.getMessageType() != MessageType.OKAY)
                returnMessage.setUser(null);
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
        }
    }

    /**
     *
     * @param message
     * @param socket
     */
    public synchronized void returnMessage(Message message, Socket socket) {
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
