/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mkma.signupsignin.thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
            //Closes the streams and the socket
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
