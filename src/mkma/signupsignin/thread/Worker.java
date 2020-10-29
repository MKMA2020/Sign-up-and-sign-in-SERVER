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
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mkma.signupsignin.dataaccess.SignableFactory;
import signable.Signable;
import user_message.Message;

/**
 *
 * @author 2dam
 */
public class Worker extends Thread {

    private Socket service;

    public Worker(Socket service) {
        this.service = service;
    }

    @Override
    public void run() {

        ObjectInputStream entry = null;
        InputStream input = null;
        Message signin = new Message();
        signin.setMessageType(1);

        //Opening of the entry stream
        try {
            input = service.getInputStream();
            entry = new ObjectInputStream(input);
        } catch (IOException e) {
            System.out.println(e);
        }

        //Message received and treated
        try {
            Message received = (Message) entry.readObject();
            System.out.println(received.getUser().getLogin());
            System.out.println(received.getUser().getEmail());
            SignableFactory factory = new SignableFactory();
            Signable signable = factory.getSignable();
            if (received.getMessageType() == signin.getMessageType()) 
                signable.signIn(received.getUser());
            else 
                 signable.signUp(received.getUser());
       
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            //Closes the streams and the socket
        } catch (DataBaseConnectionException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PassNotCorrectException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ServerErrorException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeOutException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UserExistsException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
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

}
