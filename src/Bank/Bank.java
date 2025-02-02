/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * this class handles all of the bank functions from creation of accounts to holding
 * a list of auction houses for the agents
 */
public class Bank {
    private static Map<Integer, Account> accountNumberList = new HashMap<>();
    private static Map<Integer, Account> accountBidList = new HashMap<>();
    private static Map<Integer, String> auctionList = new HashMap<>();
    private static Map<String, Integer> ipList = new HashMap<>();

    private static int numberAccount;
    private static int bidKey;
    private static int nextAccountNum = 0;

    /**
     * opens a new account with the name and initial deposit
     *
     * @param accountName    name the account will have
     * @param initialDeposit the initial deposit to be placed in the account
     */
    public static void openNewAccount(String accountName, double initialDeposit) {
        int accountNumber = nextAccountNum;
        nextAccountNum++;
        Account newAccount = new Account(accountName, accountNumber, initialDeposit);
        numberAccount = newAccount.getAccountNum();
        bidKey = newAccount.getBidKey();
        accountNumberList.put(newAccount.getAccountNum(), newAccount);
        accountBidList.put(newAccount.getBidKey(), newAccount);
        System.out.println(">>>>>>>>>>>>>"+accountBidList.get(newAccount.getBidKey()));
    }

    /**
     * opens a new auction account with the name, initial deposit, what port and host
     * the auction is on
     *
     * @param accountName    name of the auction
     * @param initialDeposit initial deposit should be 0
     * @param port           port the auction is running on
     * @param hostName       host the auction is running on
     */
    public static void openNewAuctionAccount(String accountName, double initialDeposit, String port, String hostName) {
        int accountNumber = nextAccountNum;
        nextAccountNum++;
        Account newAccount = new Account(accountName, accountNumber, initialDeposit);
        numberAccount = newAccount.getAccountNum();
        accountNumberList.put(newAccount.getAccountNum(), newAccount);
        ipList.put(port+"/"+hostName,accountNumber);
        auctionList.put(accountNumber, "" + accountName + "," + hostName + "," + port);
    }

    /**
     * returns account number based on the ip
     * @param ip of the auction house
     * @return
     */
    public static int getAccountNumberFromIp(String ip){
        return ipList.get(ip);
    }
    /**
     * gets the initialized account number
     *
     * @return and int representing the account
     */
    public static int getAccountNumber() {
        return numberAccount;
    }

    /**
     * getter to hold a bidkey when we need it
     *
     * @return bid key value
     */
    public static int getBidKey() {
        return bidKey;
    }

    /**
     * closes an account by removing it from the account list
     *
     * @param accountNumber which account to close
     */
    public static void closeAccount(int accountNumber) {
        accountNumberList.remove(accountNumber);
        auctionList.remove(accountNumber);
    }

    /**
     * gets an account from the account map
     *
     * @param accountNumber account to be gotten
     * @return account that has been chosen
     */
    private static Account getAccount(int accountNumber) {
        return accountNumberList.get(accountNumber);
    }

    /**
     * sets a hold on an account after a bid is made
     *
     * @param bidKey account to be locked
     * @param bid    amount to be locked
     */
    public static synchronized void setAccountHold(int bidKey, double bid) {
        accountBidList.get(bidKey).setAmountLocked(bid);
    }

    /**
     * unlocks the money when an agent loses a bid
     *
     * @param bidKey account to be unlocked
     */
    public static synchronized void unlockAccount(int bidKey,double amount) {
        accountBidList.get(bidKey).resetAccountHolds(amount);
    }

    /**
     * returns the balance of an account
     *
     * @param accountNumber number to get checked
     * @return double representing the balance of the account
     */
    public static double getBalance(int accountNumber) {
        Account tempAccount = getAccount(accountNumber);
        return tempAccount.getBalance();
    }

    /**
     * checks if a given account has the funds for a bid
     *
     * @param bidKey account number to check
     * @param bid    amount to check for
     * @return boolean stating whether or not theres enough cash
     */
    public static boolean hasEnoughFunds(int bidKey, double bid) {
        return accountBidList.get(bidKey).hasFunds(bid);
    }

    /**
     * checks if the account number is valid
     *
     * @param accountNumber the accunt number to be checked
     * @return boolean of whether or not the the account number is valid
     */
    public static boolean isValidNumber(int accountNumber) {
        if (accountNumberList.containsKey(accountNumber)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * returns the string representation of the accounts details
     *
     * @param numberAccount account we want the details of
     * @return string that holds the account details
     */
    public static String getAccountDetails(int numberAccount) {
        Account tempAccount = getAccount(numberAccount);
        return tempAccount.getAccountDetails();
    }

    /**
     * returns the string representation of the auction accounts details
     *
     * @param numberAccount account we want the details of
     * @return string that holds the account details
     */
    public static String getAuctionAccountDetails(int numberAccount) {
        Account auctionAccount = getAccount(numberAccount);
        return auctionAccount.getAuctionAccountDetails();
    }

    /**
     * creates a string of all the auction houses available
     *
     * @return string of the available auction houses
     */
    public static String getAuctionString() {
        String auctionString = "";
        for (String value : auctionList.values()) {
            auctionString += value + ";";
        }
        return auctionString;
    }

    /**
     * transfer money from one account to the other
     *
     * @param numberA the account it is transferring from
     * @param numberB the account it is transferring to
     * @param amount  amount being transferred
     */
    public static void moveMoney(int numberA, int numberB, double amount) {
        Account A = accountNumberList.get(numberA);
        Account B = accountNumberList.get(numberB);

        log("-----------------------");
        log("Before Transaction: ");
        A.printAccount();
        B.printAccount();

        A.withdraw(amount);
        B.deposit(amount);

        log("-----------------------");
        log("After Transaction: ");
        A.printAccount();
        B.printAccount();
    }

    /**
     * im really lazy
     *
     * @param msg string to be printed
     */
    private static void log(String msg) {
        System.out.println(msg);
    }
}
