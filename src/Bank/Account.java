package Bank;

import java.io.Serializable;

public class Account implements Serializable {
    private String name;
    private int accountNum;
    private double balance;
    private int bankKey;

    public Account(String name, int accountNum, double balance) {
        this.name = name;
        this.accountNum = accountNum;
        this.balance = balance;
        this.bankKey = hashCode();
    }

    public synchronized void deposit(double amount) {
        log("Depositing $" + amount + "into " + accountNum);
        balance += amount;
    }

    public void log(String msg) {
        System.out.println(msg);
    }
}
