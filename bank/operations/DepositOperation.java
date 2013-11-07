package bank.operations;

import bank.BankState;

public class DepositOperation extends Operation {
    int account;
    double amount;
    
    public DepositOperation (int account, double amount, String text) {
        super(text);
        this.amount = amount;
        this.account = account;
    }
    
    public void perform (BankState bank) {
        bank.deposit(account, amount);
    }
}
