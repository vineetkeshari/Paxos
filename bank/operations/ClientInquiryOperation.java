package bank.operations;

import bank.BankState;

public class ClientInquiryOperation extends Operation {
    
    int requestor;
    
    public ClientInquiryOperation(int requestor, String text) {
        super(text);
        this.requestor = requestor;
    }
    
    public void perform (BankState bank) {
        bank.inquiry(requestor);
    }
}
