/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.io.Serializable;

/**
 * This class is for passing instructions from one part of the system to the other
 * Handles a request with the attached data
 * Needs to serializable in order to send an object through a socket
 * Only message objects will be passed over the connection
 */
public class Message implements Serializable {
    public String dataInfo;
    public String data;

    /**
     * holds data to send a message object between classes
     * @param request action to be done be message receiver
     * @param data data used when message is received
     */
    public Message(String dataInfo, String data) {
        this.dataInfo = dataInfo;
        this.data = data;
    }
}
