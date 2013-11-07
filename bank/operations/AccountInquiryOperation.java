package bank.operations;

import bank.BankState;

public class AccountInquiryOperation extends Operation {
    
    int requestor;
    int accountID;
    
    public AccountInquiryOperation(int requestor, int accountID, String text) {
        super(text);
        this.requestor = requestor;
        this.accountID = accountID;
    }
    
    public void perform (BankState bank) {
        bank.inquiry(requestor, accountID);
    }
}
