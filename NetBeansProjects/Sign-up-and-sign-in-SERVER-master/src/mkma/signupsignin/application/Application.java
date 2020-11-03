package mkma.signupsignin.application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import mkma.signupsignin.thread.Worker;

/**
 * Main for the server side. It creates a server socket that listens for clients
 * petitions and creates a thread in order to attend them. It is in constant
 * loop until manually stopped.
 *
 * @author Kerman Rodríguez and Martín Gros
 */
public class Application {

    /**
     * Main method. It creates the listening server socket which then creates
     * the needed threads.
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        //Variable declaration
        boolean loop = true;
        ServerSocket server;
        server = new ServerSocket(6302);
        Socket service = null;
        while (loop) {
            //Opening of the server socket
            try {
                service = server.accept();
                Worker thread = new Worker(service);
                new Thread(thread).start();
            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }
}
