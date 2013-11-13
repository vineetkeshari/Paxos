package paxos;

import bank.operations.Operation;

public class Command {
	ProcessId client;
	int req_id;
	Operation op;

	public Command(ProcessId client, int req_id, Operation op){
		this.client = client;
		this.req_id = req_id;
		this.op = op;
	}

	public boolean equals(Object o) {
		Command other = (Command) o;
		return client.equals(other.client) && req_id == other.req_id && op.equals(other.op);
	}
	
	public int hashCode() {
	    return client.hashCode() + req_id + op.hashCode();
	}

	public String toString(){
		return "Command(" + client + ", " + req_id + ", " + op + ")";
	}
}
