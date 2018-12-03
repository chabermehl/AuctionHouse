/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Outward facing bank class that is interacted with
 * Created in BankGUI as a "proxy"
 * Implements runnable as it has a socket connection
 */
public class BankClient implements Runnable {

    public Socket agentSocket;
    public ObjectOutputStream oos;
    public ObjectInputStream ois;

    public BankClient(Socket agentSocket) throws IOException {
        this.agentSocket = agentSocket;
        this.oos = new ObjectOutputStream(agentSocket.getOutputStream());
        this.ois = new ObjectInputStream(agentSocket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = (Message) ois.readObject();
                if (message.data != null && !message.data.isEmpty()) {
                    String[] inMessage = message.data.split(",");
                    if (message.data.contains("Initialize Account")) {
                        if (inMessage.length != 3) {
                            message = new Message("Incorrect Input", "Incorrect Input");
                        } else {

                            Bank.openNewAccount(inMessage[1], Double.parseDouble(inMessage[2]));
                            String bankKey = Integer.toString(Bank.getBankKey());
                            message = new Message("Bank Key: ", bankKey);
                        }
                    }
                    if(message.data.contains("Balance")) {
                        int bankKey = Integer.parseInt(inMessage[1]);
                        log("" + bankKey);
                        if(Bank.isValidKey(bankKey)) {
                            message = new Message("Info", Bank.getAccountDetails(bankKey));
                        } else {
                            message = new Message("Error", "Invalid Bank Key");
                        }
                    }
                }
                this.sendMessage(message);
            }
        } catch (IOException e) {
            System.out.println(agentSocket.getRemoteSocketAddress() + " has disconnected");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        oos.writeObject(message);
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
