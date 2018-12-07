package AuctionHouse;

import Bank.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageReceiver implements Runnable {
    private ObjectInputStream inStream;
    private boolean active = true;
    private LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();


    public MessageReceiver(ObjectInputStream stream) {
        inStream = stream;
    }

    @Override
    public void run() {
        while(active) {
            try {
                Message message = (Message)inStream.readObject();
                messageQueue.put(message);
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutDown() {
        active = false;
    }

    public synchronized Message pollNextMessage() {
        return messageQueue.poll();
    }

    public synchronized Message waitNextMessage() {
        Message message = null;
        try {
            message =  messageQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }

    public boolean getActive() {
        return active;
    }
}
