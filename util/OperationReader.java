package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import bank.operations.*;

public class OperationReader {
    BufferedReader reader;
    boolean eof = true;
    boolean ro;
    
    public OperationReader (String filename, boolean ro) {
        try {
            reader = new BufferedReader (new FileReader (filename));
            eof = false;
            this.ro = ro;
        } catch (FileNotFoundException e) {
            System.out.println("File " + filename + " not found!");
        }
    }
    
    public Operation getOperation() {
        if (reader == null || eof)
            return null;
        Operation nextOperation = null;
        while (!eof && nextOperation == null) {
            String line = null;
            try {
                line = reader.readLine();
            } catch (IOException e) {
                System.out.println("IOException!");
            }
            if (line == null) {
                eof = true;
            } else {
                nextOperation = parseLine (line);
            }
        }
        return nextOperation;
    }
    
    private Operation parseLine (String line) {
        Operation nextOperation = null;
        String[] parts = line.split("\t");
        if (parts.length == 2) {
            if (parts[0].equals("CREATE ACCOUNT")) {
                nextOperation = new CreateAccountOperation(Integer.parseInt(parts[1]), line);
            } else if (parts[0].equals("INQUIRY")) {
                nextOperation = new ClientInquiryOperation(Integer.parseInt(parts[1]), line, ro);
            }
        } else if (parts.length == 3) {
            if (parts[0].equals("CREATE CLIENT")) {
                nextOperation = new CreateClientOperation(parts[1], Integer.parseInt(parts[2]), line);
            } else if (parts[0].equals("CREATE ACCOUNT")) {
                nextOperation = new CreateAccountForClientOperation(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), line);
            } else if (parts[0].equals("ADD ACCOUNT")) {
                nextOperation = new AddAccountOperation(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), line);
            } else if (parts[0].equals("DEPOSIT")) {
                nextOperation = new DepositOperation(Integer.parseInt(parts[1]), Double.parseDouble(parts[2]), line);
            } else if (parts[0].equals("INQUIRY")) {
                nextOperation = new AccountInquiryOperation(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), line, ro);
            }
        } else if (parts.length == 4) {
            if (parts[0].equals("WITHDRAW")) {
                nextOperation = new WithdrawOperation(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Double.parseDouble(parts[3]), line);
            }
        } else if (parts.length == 5) {
            if (parts[0].equals("TRANSFER")) {
                nextOperation = new TransferOperation(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Double.parseDouble(parts[4]), line);
            }
        }
        return nextOperation;
    }

}
