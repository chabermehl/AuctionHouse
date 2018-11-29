/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
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
            this.agentSocket = agentSocket;
            out = new PrintWriter(agentSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(agentSocket.getInputStream()));
    }

    @Override
    public void run() {
        String inputLine = null;
        String outputLine;
    }

}
