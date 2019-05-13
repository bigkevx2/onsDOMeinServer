import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConfigStateRep implements Serializable {
    // Create an instance of itself
    private static ConfigStateRep configStateRep = new ConfigStateRep();
    // the configurations repository
    private Map<String, String> configurations;
    // the state repository
    private Map<String, String> states;
    // thread locking
    private Lock repositoryLock = new ReentrantLock();
    // instance of object to read and write to file
    private ReadWriteToFile readWriteToFile = new ReadWriteToFile();
    private final String CONFIG_FILE = "configurations.dat";
    private final String STATE_FILE = "states.dat";

    /**
     * private constructor so no other object can instantiate.
     * While constructing this Singleton read configurations and states from file or create them if no file present.
     * If no file is present a new HashMap repository will be created.
     */
    private ConfigStateRep() {
        try {
            // if a file exists it will be read into the configurations hashMap
            configurations = readWriteToFile.readFromFile(CONFIG_FILE);

        } catch (IOException | ClassNotFoundException ex) {
            // else a new one will be created
            configurations = new HashMap<>();
        }
        try {
            states = readWriteToFile.readFromFile(STATE_FILE);

        } catch (IOException | ClassNotFoundException ex) {
            states = new HashMap<>();
        }
    }

    /**
     * Static method to get this instance
     * @return this instance
     */
    static ConfigStateRep getConfigStateRep() {
        return configStateRep;
    }

    /**
     * Method to get a hc configuration from the repository
     * Uses thread locking
     * @param hc, the hc_id
     * @return String with configuration of hc or NULL
     */
    public String getConfiguration(String hc) {
        String config;
        repositoryLock.lock();
        try {
            config = configurations.get(hc);
        } finally {
            repositoryLock.unlock();
        }
        return config;
    }

    /**
     * Method to save a hc configuration to the repository
     * Uses thread locking
     * @param hc, the hc_id
     * @param configuration, the configuration String with parameters.
     * @return confirmation of save
     */
    public String setConfiguration(String hc, String configuration) {
        String response;
        repositoryLock.lock();
        try {
            configurations.put(hc,configuration);
            saveConfigs();
            response = "setConfigOK";
        } catch (Exception e) {
            response = "setConfigNOK";
        } finally {
            repositoryLock.unlock();
        }
        return response;
    }

    /**
     * Method to get the state from the repository
     * Uses thread locking
     * @param hc, the hc_id
     * @return String with the state of the hc or NULL
     */
    public String getState(String hc) {
        String state;
        repositoryLock.lock();
        try {
            state = states.get(hc);

        } finally {
            repositoryLock.unlock();
        }
        return state;
    }

    /**
     * Method to save a hc state to the repository
     * Uses thread locking
     * @param hc, the hc_id
     * @param state, the state String with parameters.
     * @return confirmation of save
     */
    public String setState(String hc, String state) {
        String response;
        repositoryLock.lock();
        try {
            states.put(hc,state);
            saveStates();
            response = "setStateOK";
        } catch (Exception e) {
            response = "setStateNOK";
        } finally {
            repositoryLock.unlock();
        }
        return response;
    }

    public void saveConfigs() {
        try {
            readWriteToFile.writeToFile(configurations, CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("saveConfigs gaat fout: " + e);
        }
    }

    public void saveStates() {
        try {
            readWriteToFile.writeToFile(states, STATE_FILE);
        } catch (IOException e) {
            System.out.println("saveStates gaat fout: " + e);
        }
    }
}
