package bank.operations;

import bank.BankState;

public class AccountInquiryOperation extends Operation {
    
    int requestor;
    int accountID;
    boolean ro;
    
    public AccountInquiryOperation(int requestor, int accountID, String text, boolean ro) {
        super(text);
        this.requestor = requestor;
        this.accountID = accountID;
        this.ro = ro;
    }
    
    public void perform (BankState bank) {
        bank.inquiry(requestor, accountID);
    }
    
    @Override
    public boolean ro() {
        if (ro)
            return true;
        else
            return false;
    }
}
