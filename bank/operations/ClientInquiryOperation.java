package bank.operations;

import bank.BankState;

public class ClientInquiryOperation extends Operation {
    
    int requestor;
    boolean ro;
    
    public ClientInquiryOperation(int requestor, String text, boolean ro) {
        super(text);
        this.requestor = requestor;
        this.ro = ro;
    }
    
    public void perform (BankState bank) {
        bank.inquiry(requestor);
    }
    
    @Override
    public boolean ro() {
        if (ro)
            return true;
        else
            return false;
    }
}
