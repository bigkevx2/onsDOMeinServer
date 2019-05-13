import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class to listen for incoming server connections, if a connection is made a Client object is created and put in an ArrayList for future use.
 * For every connecting client object a thread is started.
 */
class Server {
    private ConnectedClients connectedClients = new ConnectedClients();
    // Object to log server activity to the server home screen
    private ScreenLogRep screenLogRep = ScreenLogRep.getScreenLogRep();

    /**
     * Constructor to get the ball rolling
     */
    Server() {
        try {
            screenLogRep.setConsoleAreaText("Server started");
            letClientsConnect();
        } catch (IOException e) {
            screenLogRep.setConsoleAreaText("EXCEPTION while setting up a ServerSocket: " + e);
        }
    }

    /**
     * Infinite method for listening and accepting client connections
     * @throws IOException, exception catched in constructor.
     */
    private void letClientsConnect() throws IOException {
        final int SBAP_PORT = 8888;
        String client_id;
        Client client = null;
        ServerSocket server = new ServerSocket(SBAP_PORT);
        screenLogRep.setConsoleAreaText("Waiting for clients to connect...");
//        System.out.println("Waiting for clients to connect...");

        while (true) {
            Socket socket = server.accept();
            Scanner scannerIn = new Scanner (socket.getInputStream());
            PrintWriter printWriterOut = new PrintWriter(socket.getOutputStream());

            // Handshake with connecting client
            printWriterOut.print("Who are you?\n");
            printWriterOut.flush();
            boolean response = true;
            while (response) {
                client_id = scannerIn.nextLine();
                response = false;
                client = new Client(connectedClients, client_id, scannerIn, printWriterOut);
                connectedClients.addClient(client);
                screenLogRep.setConsoleAreaText("Client: " + client_id + " connected to server");
            }
            Thread t = new Thread(client);
            t.start();
        }
    }
}
