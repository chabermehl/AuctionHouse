/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.util.HashMap;
import java.util.Map;

public class Bank {
    public static Map<Integer, Account> accountList = new HashMap<>();
    public static Map<Integer, Account> auctionList = new HashMap<>();

    public static int bankKey;

    /**
     * opens a new account with the name and initial deposit
     *
     * @param accountName    name the account will have
     * @param initialDeposit the initial deposit to be placed in the account
     */
    public static void openNewAccount(String accountName, double initialDeposit, String accountType) {
        if ("Agent".equals(accountType)) {
            int accountNumber = 12348 + accountList.size() + 1;
            Account newAccount = new Account(accountName, accountNumber, initialDeposit);
            bankKey = newAccount.getBankKey();
            accountList.put(bankKey, newAccount);
        } else if ("Auction".equals(accountType)) {
            int accountNumber = 84321 + auctionList.size() + 1;
            Account newAccount = new Account(accountName, accountNumber, 0);
            bankKey = newAccount.getBankKey();
            accountList.put(bankKey, newAccount);
        }
    }

    public static int getBankKey() {
        return bankKey;
    }

    public static Account getAccount(int bankKey) {
        return accountList.get(bankKey);
    }

    public static synchronized void setAccountHold(int bankKey, double bid) {
        accountList.get(bankKey).setAmountLocked(bid);
    }

    public static synchronized void unlockAccount(int bankKey) {
        accountList.get(bankKey).resetAccountHolds();
    }

    public static double getBalance(int bankKey) {
        Account tempAccount = getAccount(bankKey);
        return tempAccount.getBalance();
    }

    public static boolean hasEnoughFunds(int bankKey, double bid) {
        return getAccount(bankKey).hasFunds(bid);
    }

    public static boolean isValidKey(int bankKey) {
        if (accountList.containsKey(bankKey)) {
            return true;
        } else {
            return false;
        }
    }

    public static String getAccountDetails(int bankKey) {
        Account tempAccount = getAccount(bankKey);
        return tempAccount.getAccountDetails();
    }

    public static void moveMoney(int keyA, int keyB, double amount) {
        Account A = accountList.get(keyA);
        Account B = accountList.get(keyB);

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
