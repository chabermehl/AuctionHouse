/**
 * Created by chabermehl
 * 11/29/18
 * CS 351
 */
package Bank;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bank {
    public Map<Integer, Account> accountList = new HashMap<>();
    public Random rand = new Random();

    public int bankKey;

    /**
     * opens a new account with the name and initial deposit
     *
     * @param accountName    name the account will have
     * @param initialDeposit the initial deposit to be placed in the account
     */
    public void openNewAccount(String accountName, double initialDeposit) {
        int accountNumber = 1234 + accountList.size() + 1;
        Account newAccount = new Account(accountName, accountNumber, initialDeposit);
        bankKey = newAccount.getBankKey();
        accountList.put(bankKey, newAccount);
    }

    public int getBankKey() {
        return this.bankKey;
    }

    public Account getAccount(int bankKey) {
        return accountList.get(bankKey);
    }

    public synchronized void setAccountHold(int bankKey, double bid) {
        accountList.get(bankKey).setAmountLocked(bid);
    }

    public synchronized void unlockAccount(int bankKey) {
        accountList.get(bankKey).resetAccountHolds();
    }

    public double getBalance(int bankKey) {
        Account tempAccount = getAccount(bankKey);
        return tempAccount.getBalance();
    }

    public boolean hasEnoughFunds(int bankKey, double bid) {
        return getAccount(bankKey).hasFunds(bid);
    }

    public boolean isValidKey(int bankKey) {
        if (accountList.containsKey(bankKey)) {
            return true;
        } else {
            return false;
        }
    }

    public String getAccountDetails(int bankKey) {
        Account tempAccount = getAccount(bankKey);
        return tempAccount.getAccountDetails();
    }

    public void moveMoney(int keyA, int keyB, double amount) {
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

    private void log(String msg) {
        System.out.println(msg);
    }
}
