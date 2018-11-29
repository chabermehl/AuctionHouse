package Bank;

import java.io.Serializable;

public class Message implements Serializable {
    private String request;
    private String data;

    public Message(String request, String data) {
        this.request = request;
        this.data = data;
    }
}
