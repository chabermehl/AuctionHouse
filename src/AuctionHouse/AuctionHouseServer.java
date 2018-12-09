package AuctionHouse;

import Agent.BankProxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

/**
 * This is the server and waits for agents to come an connect.
 */
public class AuctionHouseServer{
    /**
     * This is the server and waits for agents to come an connect.
     * @param args name portNumber BankIp BankPort [optional: local ip]
     */
    public static void main(String [] args) {
        LinkedList<AgentProxy> agentProxies = new LinkedList<>();
        String accountInfo = "";
        BankProxy bankProxy = new BankProxy(args[2],args[3]);
        String ip = null;
        if(args.length==5){
            ip = args[4];
        }
        else {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                System.out.println("cannot get local host address");
            }
        }
        accountInfo = bankProxy.createAcount(args[0],0, ip,args[1],"Auction");
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Integer.parseInt(args[1]));
        }
        catch (IOException e){
            System.out.println("IOException in AuctionHouseServer");
            return;
        }
        AuctionHouse auctionHouse = new AuctionHouse(bankProxy,serverSocket);
        //  Listen  for  new  clients  forever
        while(true) {
            //  Create  new  thread  to  handle  each  client
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("client socket created");
                AgentProxy  agentProxy = new AgentProxy(clientSocket,auctionHouse,bankProxy);
                agentProxies.add(agentProxy);
            }
            catch (IOException e){
                break;
            }
        }
        for(AgentProxy agentProxy:agentProxies){
            agentProxy.terminate();
        }
        bankProxy.closeAccount(Integer.parseInt(
                accountInfo.split("Account Number: ")[1].split("\n")[0]));
    }
}
