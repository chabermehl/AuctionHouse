package Bank;

import java.io.Serializable;

public class Account implements Serializable {
    private String name;
    private int accountNum;
    private double balance;
    private int bankKey;
    private double moneyHeld = 0;
    private boolean locked = false;

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

    public synchronized void withdraw(double amount) {
        if(balance >= amount) {
            System.out.println(Thread.currentThread().getName() + " will withdraw " + amount + " from " + accountNum);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            balance -= amount;
        } else {
            log("Cannot withdraw " + amount + " from " + balance);
        }
    }

    public void setAmountLocked(double amount) {
        moneyHeld += amount;
        locked = true;
    }

    private void log(String msg) {
        System.out.println(msg);
    }
}
