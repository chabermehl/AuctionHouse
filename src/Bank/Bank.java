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
}
