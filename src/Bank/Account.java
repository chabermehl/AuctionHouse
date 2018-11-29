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

    /**
     * deposits money into the account
     * @param amount the amount being deposited
     */
    public synchronized void deposit(double amount) {
        log("Depositing $" + amount + "into " + this.accountNum);
        this.balance += amount;
    }

    /**
     * withdraws money from the account
     * @param amount the amount being withdrawn
     */
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

    /**
     * locks the amount of money used after a bid
     * @param amount amount to be locked after a bid
     */
    public void setAmountLocked(double amount) {
        moneyHeld += amount;
        locked = true;
    }

    /**
     * checks if the account has enough funds for the bid
     * @param amount amount being checked for possible bid
     * @return boolean based on available funds
     */
    public boolean hasFunds(double amount) {
        double available =  this.balance-moneyHeld;
        if(available == amount || available > amount) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * if a bid is lost the funds must be unlocked
     */
    public void resetAccountHolds() {
        moneyHeld = 0;
        locked = false;
    }

    public double getBalance() {return this.balance;}
    public void setbalance(double newBalance) {this.balance = newBalance;}
    public int getAccountNum() {return this.accountNum;}
    public String getName() {return this.name;}
    public int getBankKey() {return this.bankKey;}

    /**
     * method for easy printing
     * @param msg message to be printed
     */
    private void log(String msg) {
        System.out.println(msg);
    }

    /**
     * overriding hashcode
     * @return hash value for the bankKey
     */
    @Override
    public int hashCode() {
        final int prime = 17;
        int result = 123456;
        result = prime * result * this.accountNum;
        return result;
    }
}
