package Bank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Bank implements Runnable {

    private Socket agentSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Bank(Socket agentSocket) throws IOException {
        try {
            this.agentSocket = agentSocket;
            out = new PrintWriter(agentSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(agentSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String inputLine = null;
        String outputLine;
    }

}
