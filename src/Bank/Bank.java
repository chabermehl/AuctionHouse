/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    public static Map<Integer, Account> accountNumberList = new HashMap<>();
    public static Map<Integer, Account> accountBidList = new HashMap<>();
    public static Map<String, String> auctionList = new HashMap<>();

    public static int numberAccount;
    public static int bidKey;

    /**
     * opens a new account with the name and initial deposit
     *
     * @param accountName    name the account will have
     * @param initialDeposit the initial deposit to be placed in the account
     */
    public static void openNewAccount(String accountName, double initialDeposit) {
        int accountNumber = 12348 + accountNumberList.size() + 1;
        Account newAccount = new Account(accountName, accountNumber, initialDeposit);
        numberAccount = newAccount.getAccountNum();
        bidKey = newAccount.getBidKey();
        accountNumberList.put(newAccount.getAccountNum(), newAccount);
        accountBidList.put(newAccount.getBidKey(), newAccount);
    }

    public static void openNewAuctionAccount(String accountName, double initialDeposit, String port, String hostName) {
        int accountNumber = 84321 + auctionList.size() + 1;
        Account newAccount = new Account(accountName, accountNumber, initialDeposit);
        numberAccount = newAccount.getAccountNum();
        accountNumberList.put(newAccount.getAccountNum(), newAccount);
        auctionList.put(accountName, "" + accountName + ";" + port + ";" + hostName);
    }

    public static int getAccountNumber() {
        return numberAccount;
    }

    public static int getBidKey() {
        return bidKey;
    }

    public static Account getAccount(int accountNumber) {
        return accountNumberList.get(accountNumber);
    }

    public static synchronized void setAccountHold(int bankKey, double bid) {
        accountBidList.get(bankKey).setAmountLocked(bid);
    }

    public static synchronized void unlockAccount(int bankKey) {
        accountBidList.get(bankKey).resetAccountHolds();
    }

    public static double getBalance(int accountNumber) {
        Account tempAccount = getAccount(accountNumber);
        return tempAccount.getBalance();
    }

    public static boolean hasEnoughFunds(int bankKey, double bid) {
        return getAccount(bankKey).hasFunds(bid);
    }

    public static boolean isValidNumber(int accountNumber) {
        if (accountNumberList.containsKey(accountNumber)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getAccountDetails(int bankKey) {
        Account tempAccount = getAccount(bankKey);
        return tempAccount.getAccountDetails();
    }

    public static String getAuctionAccountDetails(int numberAccount) {
        Account auctionAccount = getAccount(numberAccount);
        return auctionAccount.getAuctionAccountDetails();
    }

    public static String getAuctionString() {
        String auctionString = "";
        for (String value : auctionList.values()) {
            auctionString = value + ";";
        }
        return auctionString;
    }

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

    private static void log(String msg) {
        System.out.println(msg);
    }
}
