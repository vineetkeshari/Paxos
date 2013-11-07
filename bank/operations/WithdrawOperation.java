package bank.operations;

import bank.BankState;

public class WithdrawOperation extends Operation {
    int requestor;
    int account;
    double amount;
    
    public WithdrawOperation (int requestor, int account, double amount, String text) {
        super(text);
        this.requestor = requestor;
        this.amount = amount;
        this.account = account;
    }
    
    public void perform (BankState bank) {
        bank.withdraw(requestor, account, amount);
    }
}
