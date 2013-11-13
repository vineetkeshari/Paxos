package paxos;

import bank.operations.Operation;

import java.util.*;

import util.OperationReader;

public class Env {
	Map<ProcessId, Process> procs = new HashMap<ProcessId, Process>();
	public final static int nAcceptors = 5, nReplicas = 3, nLeaders = 3; // Max 2 failures

	synchronized void sendMessage(ProcessId dst, PaxosMessage msg){
		Process p = procs.get(dst);
		if (p != null) {
			p.deliver(msg);
		}
	}

	synchronized void addProc(ProcessId pid, Process proc){
		procs.put(pid, proc);
		proc.start();
	}

	synchronized void removeProc(ProcessId pid){
		procs.remove(pid);
	}

	void run(String[] args){
	    if (args.length == 0 || args.length > 2) {
	        System.out.println("Please provide one filename and optional -RO flag");
	        System.exit(0);
	    }
	    
		ProcessId[] acceptors = new ProcessId[nAcceptors];
		ProcessId[] replicas = new ProcessId[nReplicas];
		ProcessId[] leaders = new ProcessId[nLeaders];

		for (int i = 0; i < nAcceptors; i++) {
			acceptors[i] = new ProcessId("acceptor:" + i);
			Acceptor acc = new Acceptor(this, acceptors[i]);
		}
		for (int i = 0; i < nReplicas; i++) {
			replicas[i] = new ProcessId("replica:" + i);
			Replica repl = new Replica(this, replicas[i], leaders);
		}
		for (int i = 0; i < nLeaders; i++) {
			leaders[i] = new ProcessId("leader:" + i);
			Leader leader = new Leader(this, leaders[i], acceptors, replicas);
		}
		
		OperationReader reader = null;
		if (args.length == 2)
		    if (args[1].equals("-RO"))
		        reader = new OperationReader(args[0], true);
		    else {
		        System.out.println("Unidentified argument 2!");
		        System.exit(0);
		    }
		else
		    reader = new OperationReader(args[0], false);
		Operation operation = reader.getOperation();;
		for (int i=1; operation != null; ++i) {
			ProcessId pid = new ProcessId("Client ID: " + i);
			for (int r = 0; r < nReplicas; r++) {
				sendMessage(replicas[r], new RequestMessage(pid, new Command(pid, 0, operation)));
			}
			operation = reader.getOperation();
		}
	}

	public static void main(String[] args){
		new Env().run(args);
	}
}
