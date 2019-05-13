import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton class to collect logQueries for the db so the db is not called after each query
 */
class DbRep {
    // Create an instance of itself
    private static DbRep dbRep = new DbRep();
    private ArrayList<String> logQueries = new ArrayList<>();
    // db calls handled in threat pool to share resources
    private ExecutorService pool = Executors.newFixedThreadPool(10);
    // to avoid race conditions with multiple threads
    private Lock repositoryLock = new ReentrantLock();
    // Timer to clean up the repo if server is idle, now set for 3 seconds after last call to addLog
    private Timer timer = new Timer(3000, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            // Code to be executed after timer fires
            // Check if there is anything in the repo
            if (logQueries.size() > 0) {
                // write to db if so
                writeLogToDb();
            }
        }
    });

    /**
     * Private constructor so no other class can instantiate
     */
    private DbRep() {
        // start the timer
        timer.setRepeats(true);
        timer.start();
    }

    /**
     * Static method to get this instance
     * @return this instance
     */
    static DbRep getDbRepInstance() {
        return dbRep;
    }

    private void addLogQueryToRepo(String query) {
        // The timer to write to the db is only fired if nothing is happening here, so restart after each call to addLog
        timer.restart();
        repositoryLock.lock();
        try {
            // add a single query to the container
            logQueries.add(query);
            // if container reaches 1000 logQueries write them to the db
            if (logQueries.size() == 100) {
                writeLogToDb();
            }
        } finally {
            // always unlock the repo so other threads can access it.
            repositoryLock.unlock();
        }
    }

    /**
     * Build one query and add it to a container so the query is not executed directly
     * @param t1, timestamp of server entry
     * @param t2, timestamp of server exit
     * @param sender, who is sending a message via this server
     * @param receiver, who is receiving this message
     * @param command, what is the command? a get, set or response?
     * @param cmdMessage, what is the message
     */
    void addLog(Timestamp t1, Timestamp t2, String sender, String receiver, String command, String cmdMessage) {
        String query = "INSERT INTO dbo.ServerLog (InServerTimeStamp, OutServerTimeStamp, SendingClient, ReceivingClient, Command, CmdMessage) " +
                "VALUES ('" + t1 + "','" + t2 + "','" + sender + "','" + receiver + "','" + command + "','" + cmdMessage + "')";
        addLogQueryToRepo(query);
    }

    private void writeLogToDb() {
        // using a StringBuilder because it's more efficient than String +=
        StringBuilder buildQuery = new StringBuilder();
        // Build one query from the container of logQueries.
        for (String q : logQueries) {
            buildQuery.append(q);
        }
        // After the query is build empty the repo
        logQueries.clear();
        // transform the StringBuilder to a String
        String query = buildQuery.toString();
        // Start an instance of dbConnect to connect to the db
        DbConnect dbConnect = new DbConnect(query);
        pool.execute(dbConnect);
    }
}
