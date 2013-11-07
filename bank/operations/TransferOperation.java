package bank.operations;

import bank.BankState;

public class TransferOperation extends Operation {
    int requestor;
    int accountFrom;
    int accountTo;
    double amount;
    
    public TransferOperation (int requestor, int accountFrom, int accountTo, double amount, String text) {
        super(text);
        this.requestor = requestor;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
    }
    
    public void perform (BankState bank) {
        bank.transfer(requestor, accountFrom, accountTo, amount);
    }
}
