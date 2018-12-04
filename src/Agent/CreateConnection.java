package Agent;

import java.net.Socket;

public class CreateConnection implements Runnable {
    public String name;
    public Socket socket;
    public String address;
    public int port;

    public CreateConnection(String name, Socket socket, String address, int port) {
        this.name = name;
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {

    }

}
