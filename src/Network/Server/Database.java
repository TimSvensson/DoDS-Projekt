package Network.Server;

import java.util.ArrayList;

/**
 * Created by timsvensson on 10/12/16.
 */
public class Database {

    volatile private ArrayList<String> log;

    public Database() {

        this.log = new ArrayList<>();
    }

    public ArrayList<String> getLog() {
        return this.log;
    }

    public String getLogAt(int i) {
        return this.log.get(i);
    }

    public synchronized void writeToLog(String s) {
        System.out.println("Writing to log: " + s);
        this.log.add(s);
    }

    public int Size() {
        return this.log.size();
    }
}
