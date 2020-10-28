package mkma.signupsignin.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mkma.signupsignin.thread.Worker;
import user_message.Message;

/**
 * Main for the server side. It creates a server socket that listens
 * for clients petitions and creates a thread in order to attend them.
 * It is in constant loop until manually stopped.
 * @author Kerman Rodríguez and Martín Gros
 */
public class Application {

    /**
     * Main method. It creates the listening server socket
     * which then creates the needed threads.
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        //Variable declaration
        boolean loop = true;
        while(loop){
        ServerSocket server;
        Socket service = null;
        ObjectInputStream entry = null;
        InputStream input = null;
        //Opening of the server socket
        try {
            server = new ServerSocket(6302);
            service = server.accept();
        } catch (IOException e) {
            System.out.println(e);
        }
        
        //Opening of the entry stream
        try {
            input = service.getInputStream();
            entry = new ObjectInputStream(input);
        } catch(IOException e) {
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
        }finally {
            try {
                input.close();
            } catch (IOException ex) {
                System.out.println (ex.getMessage());
            }
            try {
                entry.close();
            } catch (IOException ex) {
                System.out.println (ex.getMessage());
            }
            try {
                service.close();
            } catch (IOException ex) {
                System.out.println (ex.getMessage());
            }           
        }   
        }   
    }
    
}
