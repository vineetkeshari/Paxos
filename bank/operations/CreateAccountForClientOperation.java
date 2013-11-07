package bank.operations;

import bank.BankState;

public class CreateAccountForClientOperation extends Operation {
    
    int clientID;
    int accountID;
    
    public CreateAccountForClientOperation(int clientID, int accountID, String text) {
        super(text);
        this.clientID = clientID;
        this.accountID = accountID;
    }
    
    public void perform (BankState bank) {
        bank.createAccount(clientID, accountID);
    }
}
