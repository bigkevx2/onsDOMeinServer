import java.io.*;
import java.util.Map;

public class ReadWriteToFile {

    ReadWriteToFile() {
    }

    public void writeToFile(Map stateMap, String fileName) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
        out.writeObject(stateMap);
        out.close();
    }

    public Map<String, String> readFromFile(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName));
        Map<String, String> map = (Map<String, String>) in.readObject();
        in.close();
        return map;
    }
}
