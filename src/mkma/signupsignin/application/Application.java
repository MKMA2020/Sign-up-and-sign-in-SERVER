package mkma.signupsignin.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ResourceBundle;

import mkma.signupsignin.dataaccess.*;
import mkma.signupsignin.thread.Worker;

/**
 * Main for the server side. It creates a server socket that listens for clients
 * petitions and creates a thread in order to attend them. It is in constant
 * loop until manually stopped.
 *
 * @author Kerman Rodríguez and Martín Gros
 */
public class Application {
    public static ResourceBundle configFile;
    
        
     public static Integer maxClients;

    /**
     * Main method. It creates the listening server socket which then creates
     * the needed threads.
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //Variable declaration
        configFile = ResourceBundle.getBundle("mkma.signupsignin.dataaccess.config");
        maxClients = (Integer.parseInt(configFile.getString("MaxClients")));
        boolean loop = true;
        ServerSocket server;
        server = new ServerSocket(6302);
        Socket service = null;
        while (loop) {
            //Opening of the server socket
            try {
                service = server.accept();
                if(maxClients>0) {
                    Worker thread = new Worker(service, Boolean.TRUE);
                    new Thread(thread).start();
                } else {
                     Worker thread = new Worker(service, Boolean.FALSE);
                    new Thread(thread).start();
                }
                    
                
                
            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }
    /**
     * This method will remove one from the maximum clients when one
     * tries to use the app.
     */
    public synchronized static void getConnection() {
       maxClients--;
    }
    /**
     * This method will add one from the maximum clients when 
     * a client has finished.
     */
    public synchronized static void releaseConnection() {
        maxClients++;
    }
}
