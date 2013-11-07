package bank.operations;

import bank.BankState;

public class CreateClientOperation extends Operation {
    String name;
    int clientID;
    
    public CreateClientOperation (String name, int clientID, String text) {
        super(text);
        this.name = name;
        this.clientID = clientID;
    }
    
    public void perform (BankState bank) {
            bank.createClient(name, clientID);
    }
}
