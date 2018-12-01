/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.io.*;
import java.net.Socket;

/**
 * Outward facing bank class that is interacted with
 * Created in BankServer as a "proxy"
 * Implements runnable as it has a socket connection
 */
public class BankClient implements Runnable {

    private Socket agentSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public BankClient(Socket agentSocket) throws IOException {
            this.agentSocket = agentSocket;
            this.oos = new ObjectOutputStream(agentSocket.getOutputStream());
            this.ois = new ObjectInputStream(agentSocket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while(true) {
                Message m = (Message) ois.readObject();
            }
        } catch (IOException e) {
            System.out.println(agentSocket.getRemoteSocketAddress()+" has disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        oos.writeObject(message);
    }



}
