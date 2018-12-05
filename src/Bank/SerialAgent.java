/**
 * Created by chabermehl
 * 12/04/18
 * CS 351
 */
package Bank;

import java.util.LinkedList;
import java.util.List;

public class SerialAgent {

    public String name;
    public int bankKey;
    public int bidKey;
    public List<String> items = new LinkedList<>();

    public SerialAgent(String name) {
        this.name = name;
    }

    public void setBankKey(int bankKey) {
        this.bankKey = bankKey;
    }

    public void setBidKey(int bidKey) {
        this.bidKey = bidKey;
    }

    public int getBankKey() {
        return this.bankKey;
    }

    public int getBidKey() {
        return this.bidKey;
    }

    public String getName() {
        return this.name;
    }

    public void itemWon(String item) {
        items.add(item);
    }
}
