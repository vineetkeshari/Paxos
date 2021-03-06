package bank;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import paxos.ProcessId;

public class BankState {
    Map<Integer,Client> clients;
    Map<Integer,Account> accounts;
    Set<Ownership> ownerships;
    ProcessId processId;
    BufferedWriter logger;
    
    public BankState(ProcessId processId) {
        clients = new HashMap<Integer, Client>();
        accounts = new HashMap<Integer, Account>();
        ownerships = new HashSet<Ownership>();
        this.processId = processId;
        try {
            this.logger = new BufferedWriter (new FileWriter ("log/" + processId.toString() + ".log"));
        } catch (IOException e) {
            System.out.println("IOEXception!");
        }
    }
    
    private void wrapPrint (String s) {
        try {
            logger.write(s + "\n");
            logger.flush();
        } catch (IOException e) {
            System.out.println("IOEXception!");
        }
    }
    
    public void createClient (String name, int id) {
        if (clients.containsKey(id)) {
            wrapPrint ("Client ID " + id + " already exists");
        } else {
            wrapPrint("Creating client with ID " + id);
            clients.put(id,new Client(id, name));
        }
    }
    
    public void createAccount (int id) {
        if (accounts.containsKey(id)) {
            wrapPrint ("Account ID " + id + " already exists");
        } else {
            wrapPrint("Creating account with ID " + id);
            accounts.put(id, new Account(id));
        }
    }
    
    public void createAccount (int client, int id) {
        if (!clients.containsKey(client)) {
            wrapPrint("Client " + client + " not found");
        } else if (accounts.containsKey(id)) {
            wrapPrint ("Account ID " + id + " already exists");
        } else {
            createAccount(id);
            addAccount(client, id);
        }
    }
    
    public void addAccount (int client, int account) {
        if (!clients.containsKey(client)) {
            wrapPrint("Client " + client + " not found");
        } else if (!accounts.containsKey(account)) {
            wrapPrint("Account " + account + " not found!");
        } else {
            wrapPrint("Account " + account + " added to client " + client);
            ownerships.add(new Ownership(client, account));
        }
    }
    
    public void deposit (int account, double amount) {
        if (accounts.containsKey(account)) {
            wrapPrint("Adding " + amount + " to account " + account);
            accounts.get(account).addBalance(amount);
        } else {
            wrapPrint("Account " + account + " not found!");
        }
    }
    
    public boolean withdraw (int requestor, int account, double amount) {
        if (accounts.containsKey(account)) {
            if (!clients.containsKey(requestor)) {
                wrapPrint("Client " + requestor + " not found");
                return false;
            }
            if (!clients.get(requestor).getAccounts().contains(account)) {
                wrapPrint("Client " + requestor + " does not own account " + account);
                return false;
            }
            wrapPrint("Withdrawing " + amount + " from account " + account);
            if (accounts.get(account).removeBalance(amount))
                return true;
            else {
                wrapPrint("Insufficient funds!");
                return false;
            }
        } else {
            wrapPrint("Account " + account + " not found!");
            return false;
        }
    }
    
    public void transfer (int requestor, int accountFrom, int accountTo, double amount) {
        wrapPrint("Initiating transfer from " + accountFrom + " to " + accountTo);
        if (!accounts.containsKey(accountTo)) {
            wrapPrint("Account " + accountTo + " not found!");
        } else if (!accounts.containsKey(accountFrom)) {
            wrapPrint("Account " + accountFrom + " not found!");
        } else {
            if (withdraw (requestor, accountFrom, amount))
                deposit (accountTo, amount);
        }
    }
    
    public void inquiry (int requestor, int account) {
        if (!clients.containsKey(requestor)) {
            wrapPrint("Client " + requestor + " not found");
        } else if (!accounts.containsKey(account)) {
            wrapPrint("Account " + account + " not found");
        } else if (!clients.get(requestor).getAccounts().contains(account)) {
            wrapPrint("Client " + requestor + " does not own account " + account);
        } else
            wrapPrint ("Account balance for " + account + " is " + accounts.get(account).getBalance());
    }
    
    public void inquiry (int requestor) {
        if (!clients.containsKey(requestor)) {
            wrapPrint("Client " + requestor + " not found");
        } else {
            Set<Integer> accounts = clients.get(requestor).getAccounts();
            for (Integer a : accounts)
                inquiry(requestor, a);
        }
    }
    
    class Client {
        String name;
        int clientID;
        
        public Client (int id, String name) {
            this.name = name;
            clientID = id;
        }
        
        public String getName() {
            return name;
        }
        
        public Set<Integer> getAccounts() {
            Set<Integer> myAccounts = new HashSet<Integer>();
            for (Ownership o : ownerships) {
                if (o.clientID == clientID)
                    myAccounts.add(o.accountID);
            }
            return myAccounts;
        }
        
        public String toString() {
            return "Client " + clientID + " is " + name;
        }
    }
    
    class Account {
        double balance;
        int accountID;
        
        public Account (int id) {
            accountID = id;
        }
        
        public double getBalance() {
            return balance;
        }
        
        public void addBalance(double value) {
            balance += value;
        }
        
        public boolean removeBalance(double value) {
            if (balance >= value) {
                balance -= value;
                return true;
            } else
                return false;
        }
        
        public String toString() {
            return "Account " + accountID + " has balance " + balance;
        }
    }
    
    class Ownership {
        int accountID;
        int clientID;
        
        public Ownership (int client, int account) {
            accountID = account;
            clientID = client;
        }
        
        public String toString () {
            return "Client " + clientID + " owns account " + accountID;
        }
    }
    
    public String toString () {
        StringBuffer sb = new StringBuffer();
        sb.append("ACCOUNTS:\n");
        for (int a : accounts.keySet())
            sb.append(accounts.get(a).toString());
        sb.append("\nCLIENTS:\n");
        for (int c : clients.keySet())
            sb.append(clients.get(c).toString());
        sb.append("\nOWNERSHIPS:\n");
        for (Ownership o : ownerships)
            sb.append(o.toString());
        return new String(sb);
    }
    
    public void finalize() {
        try {
            logger.close();
        } catch (IOException e) {
            System.out.println("IOException!");
        }
    }

}
