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
        log("Depositing $" + amount + "into " + this.accountNum);
        this.balance += amount;
    }

    public synchronized void withdraw(double amount) {
        if(this.balance >= amount) {
            System.out.println(Thread.currentThread().getName() + " will withdraw " + amount + " from " + this.accountNum);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.balance -= amount;
        } else {
            log("Cannot withdraw " + amount + " from " + this.balance);
        }
    }

    public void setAmountLocked(double amount) {
        moneyHeld += amount;
        locked = true;
    }

    public boolean hasFunds(double amount) {
        double available =  this.balance-moneyHeld;
        if(available == amount || available > amount) {
            return true;
        } else {
            return false;
        }
    }

    public void resetAccountHolds() {
        moneyHeld = 0;
        locked = false;
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 123456;
        result = prime * result * this.accountNum;
        return result;
    }
}
