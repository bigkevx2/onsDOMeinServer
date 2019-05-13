import java.io.*;
import java.sql.Timestamp;
import java.util.Scanner;

public class Client implements Runnable {
    private ConnectedClients connectedClients;
    private String client_id;
    private Scanner inClient;
    private PrintWriter outClient;
    private DbRep db = DbRep.getDbRepInstance();
    private ConfigStateRep configStateRep = ConfigStateRep.getConfigStateRep();
    private ScreenLogRep screenLogRep = ScreenLogRep.getScreenLogRep();
    private String[] splitCommandString;

    /**
     * Constructor for making Client objects
     * @param connectedClients instance of ConnectedClients class, every Client object needs the same instance to get the list of connected clients.
     * @param client_id id of the connected client, could be a userapplication or huiscentrale.
     * @param inClient the instream from this client.
     * @param outClient the outstream from this client.
     */
    Client(ConnectedClients connectedClients, String client_id, Scanner inClient, PrintWriter outClient) {
        this.connectedClients = connectedClients;
        this.client_id = client_id;
        this.inClient = inClient;
        this.outClient = outClient;
    }

    /**
     * run method to get the thread running.
     */
    public void run() {
        try {
            doService();
        } catch (Exception e) {
            screenLogRep.setConsoleAreaText("IO_EXCEPTION in run: " + e);
        } finally {
            // when a tread terminates (willingly or unwillingly) the finally block makes sure
            // the client is removed from the connectedClients array.
            connectedClients.deleteClient(client_id);
            screenLogRep.setConsoleAreaText("Client: " + client_id + " disconnected from server");
        }
    }

    /**
     * listens for traffic from a particular client and determines the action requested from this client.
     */
    private void doService() {
        Timestamp timeIn;
        // doService always needs to run for listening for request for this particular client
        while (true) {
            if (inClient == null) {return;}
            if (!inClient.hasNext()) {return;}

            // what command is this instance receiving
            String command = inClient.nextLine();

            if (!command.isEmpty()) {
                // Set the 'incoming to server' time for Db
                timeIn = new Timestamp(System.currentTimeMillis());
                // debug line to see what instance is handling input
//                System.out.println("Server receives from client: " + client_id);
                screenLogRep.setConsoleAreaText("Server receives from client " + client_id + ": " + command);
//                System.out.println("Command received from client " + client_id + ": " + command);
                // split the command string up in multiple parts to use as parameters in executeCommand
                splitCommandString = command.split(";", 0);
                // check to see the array has the correct length.
                // String should look like this: par1;par2;par3;par4
                if (splitCommandString.length == 4) {
//                    System.out.println("command received by server: " + splitCommandString[0]);
                    switch (splitCommandString[0]) {
                        case "setHc":
                        case "getHc":
                            executeCommand(splitCommandString[0], splitCommandString[2], splitCommandString[3]);
                            break;
                        case "setState":
                            sendServerResponse(configStateRep.setState(splitCommandString[2],splitCommandString[3]));
                            break;
                        case "getState":
                            sendServerResponse(configStateRep.getState(splitCommandString[2]));
                            break;
                        case "setConfig":
                            sendServerResponse(configStateRep.setConfiguration(splitCommandString[2],splitCommandString[3]));
                            break;
                        case "getConfig":
                            sendServerResponse(configStateRep.getConfiguration(splitCommandString[2]));
                            break;
//                        case "quit":
//                            configStateRep.saveConfigs();
//                            configStateRep.saveStates();
//                            System.out.println("Het is gelukt om de configs en states te saven");
//                            break;
                        default:
                            incorrectProtocol();
                    }
                    // Set the 'outgoing from server' time for Db
                    Timestamp timeOut = new Timestamp(System.currentTimeMillis());
                    // Log server activity to db
                    db.addLog(timeIn,timeOut, client_id, splitCommandString[2], splitCommandString[0], splitCommandString[3]);
                } else {
                    incorrectProtocol();
                    // If connection fails also log server activity to db
                    db.addLog(timeIn, timeIn, client_id, null, null, "Not the correct protocol used");
                }
            }
        }
    }

    /**
     * Method invoked if the client does not send the correct protocol.
     */
    private void incorrectProtocol() {
        sendServerResponse("Error: not conform to protocol.");
    }

    /**
     * If the response is not from a HC but from the server itself use this method to send the response to the requesting client.
     * @param response, String with the response.
     */
    private void sendServerResponse(String response) {
        outClient.println(response);
        outClient.flush();
        screenLogRep.setConsoleAreaText("Server responds to client: " + client_id + " " + response);
    }

    /**
     * Executes a single command
     * @param requestTo the request is for
     * @param request the request
     */
    private void executeCommand(String request, String requestTo, String message) {
        // get the correct Client instance with the correct streams to reach the correct client
        Client connectedClient = connectedClients.getClient(requestTo);
        if (connectedClient != null) {
            connectedClient.outClient.println(request + ";" + client_id + ";" + message);
            connectedClient.outClient.flush();
            screenLogRep.setConsoleAreaText("Server sends to client: " + splitCommandString[2] + " " + request + ";" + client_id + ";" + message);
        } else {
            // else the server will always respond with 'no connection' so the GA will always get a response
            outClient.println("No connection with HC");
            outClient.flush();
            screenLogRep.setConsoleAreaText("Server responds to client: " + client_id  + " No connection with HC");
        }
    }

    String getClient_id() {
        return client_id;
    }
}
