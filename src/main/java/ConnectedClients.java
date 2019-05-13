import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with one ArrayList to store all connected clients.
 */
class ConnectedClients {
    private ArrayList<Client> connectedClients = new ArrayList<>();
    private Lock connectedClientsLock = new ReentrantLock();

    /**
     * Method to add a new client with unique client_id to the ArrayList
     * Method locks resource because of multi threading
     * @param client, the client to add to the arraylist
     */
    void addClient(Client client) {
        connectedClientsLock.lock();
        try {
            connectedClients.add(client);
        } finally {
            connectedClientsLock.unlock();
        }
    }

    /**
     * Method to get a client and it's streams from the arraylist
     * Method locks resource because of multi threading
     * @param client_id, the client id we want to get.
     * @return the client instance we are looking for
     */
    Client getClient(String client_id) {
        Client connectedClient = null;
        connectedClientsLock.lock();
        try {
            for (Client client : connectedClients) {
                if (client.getClient_id().equals(client_id)) {
                    connectedClient = client;
                }
            }
        } finally {
            connectedClientsLock.unlock();
        }
        return connectedClient;
    }

    /**
     * Method to delete a client from the arraylist if the client is no longer connected to the server
     * Method locks resource because of multi threading
     * @param client_id, the client that needs deletion from the arraylist
     */
    void deleteClient(String client_id) {
        connectedClientsLock.lock();
        try {
            for (int i = 0; i < connectedClients.size(); i++) {
                if (connectedClients.get(i).getClient_id().equals(client_id)) {
                    connectedClients.remove(i);
                    break;
                }
            }
        } finally {
            connectedClientsLock.unlock();
        }
    }
}
