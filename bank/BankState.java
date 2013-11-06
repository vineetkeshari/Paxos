package bank;

import paxos.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

public class BankState {
    static int accountIDSeq = 100;
    static int clientIDSeq = 200;
    Map<Integer,Client> clients;
    Map<Integer,Account> accounts;
    Set<Ownership> ownerships;
    
    public BankState() {
        clients = new HashMap<Integer, Client>();
        accounts = new HashMap<Integer, Account>();
        ownerships = new HashSet<Ownership>();
    }
    
    static synchronized private int getNextAccountID() {
        return ++accountIDSeq;
    }
    
    static synchronized private int getNextClientID() {
        return ++clientIDSeq;
    }
    
    public int createClient (String name) {
        int id = getNextClientID();
        System.out.println("Creating client with ID " + id);
        clients.put(id,new Client(id, name));
        return id;
    }
    
    public int createAccount () {
        int id = getNextAccountID();
        System.out.println("Creating account with ID " + id);
        accounts.put(id, new Account(id));
        return id;
    }
    
    public int createAccount (int client) {
        int account = createAccount();
        addAccount(client, account);
        return account;
    }
    
    public void addAccount (int client, int account) {
        System.out.println("Account " + account + " added to client " + client);
        ownerships.add(new Ownership(client, account));
    }
    
    public void deposit (int account, double amount) {
        if (accounts.containsKey(account)) {
            System.out.println("Adding " + amount + " to account " + account);
            accounts.get(account).addBalance(amount);
        } else {
            System.out.println("Account " + account + " not found!");
        }
    }
    
    public void withdraw (int requestor, int account, double amount) {
        if (accounts.containsKey(account)) {
            if (!clients.containsKey(requestor)) {
                System.out.println("Client " + requestor + " not found");
                return;
            }
            if (!clients.get(requestor).getAccounts().contains(account)) {
                System.out.println("Client " + requestor + " does not own account " + account);
                return;
            }
            System.out.println("Withdrawing " + amount + " from account " + account);
            accounts.get(account).addBalance(amount);
        } else {
            System.out.println("Account " + account + " not found!");
        }
    }
    
    public void transfer (int requestor, int accountFrom, int accountTo, double amount) {
        System.out.println("Initiating transfer from " + accountFrom + " to " + accountTo);
        withdraw (requestor, accountFrom, amount);
        deposit (accountTo, amount);
    }
    
    public void inquiry (int client, int account) {
        if (!clients.containsKey(client)) {
            System.out.println("Client " + client + " not found");
        } else if (!accounts.containsKey(account)) {
            System.out.println("Client " + account + " not found");
        } else if (!clients.get(client).getAccounts().contains(account)) {
            System.out.println("Client " + client + " does not own account " + account);
        } else
            System.out.println ("Account balance for " + account + " is " + accounts.get(account).getBalance());
    }
    
    public void inquiry (int client) {
        if (!clients.containsKey(client)) {
            System.out.println("Client " + client + " not found");
        } else {
            Set<Integer> accounts = clients.get(client).getAccounts();
            for (Integer a : accounts)
                inquiry(client, a);
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
        
        public void removeBalance(double value) {
            if (value >= balance)
                balance -= value;
            else
                System.out.println("Insufficient funds!");
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

}
