package bank.operations;

import bank.BankState;

public class CreateAccountOperation extends Operation {
    
    int accountID;
    
    public CreateAccountOperation(int accountID, String text) {
        super(text);
        this.accountID = accountID;
    }
    
    public void perform (BankState bank) {
        bank.createAccount(accountID);
    }
}
