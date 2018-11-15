package Agent;

import java.util.Scanner;

public class Agent{
    public static void main(String []args){
        BankProxy bankProxy = new BankProxy();
        int acountnum = bankProxy.createAcount(Integer.parseInt(args[1]));
        int key = bankProxy.getKey(acountnum);
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        while (!input.equals("log out")){
            //if()
            input=sc.nextLine();
        }
        bankProxy.closeAcount(acountnum);
    }
}
