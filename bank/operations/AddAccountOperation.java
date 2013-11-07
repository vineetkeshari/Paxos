package bank.operations;

import bank.BankState;

public class AddAccountOperation extends Operation {
    
    int clientID;
    int accountID;
    
    public AddAccountOperation(int clientID, int accountID, String text) {
        super(text);
        this.clientID = clientID;
        this.accountID = accountID;
    }
    
    public void perform (BankState bank) {
        bank.addAccount(clientID, accountID);
    }
}
