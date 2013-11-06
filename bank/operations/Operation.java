package bank.operations;

import bank.BankState;

public abstract class Operation {
    public abstract void perform (BankState bank);
}
