package bank.operations;

import bank.BankState;

public abstract class Operation {
    String text;
    
    public Operation (String text) {
        this.text = text;
    }
    
    public abstract void perform (BankState bank);
    
    public boolean ro () {
        return false;
    }
    
    public String text() {
        return text;
    }
    
    public String toString() {
        return text;
    }
    
    public int hashCode() {
        return text.hashCode();
    }
}
