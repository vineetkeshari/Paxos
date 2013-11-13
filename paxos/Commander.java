package paxos;

import java.util.*;

public class Commander extends Process {
	ProcessId leader;
	ProcessId[] acceptors, replicas;
	BallotNumber ballot_number;
	int slot_number;
	Command command;

	public Commander(Env env, ProcessId me, ProcessId leader, ProcessId[] acceptors,
			ProcessId[] replicas, BallotNumber ballot_number, int slot_number, Command command){
		this.env = env;
		this.me = me;
		this.acceptors = acceptors;
		this.replicas = replicas;
		this.leader = leader;
		this.ballot_number = ballot_number;
		this.slot_number = slot_number;
		this.command = command;
		env.addProc(me, this);
	}

	public void body(){
	    if (!command.op.ro()) {
    		P2aMessage m2 = new P2aMessage(me, ballot_number, slot_number, command);
    		Set<ProcessId> waitfor = new HashSet<ProcessId>();
    		for (ProcessId a: acceptors) {
    			sendMessage(a, m2);
    			waitfor.add(a);
    		}
    
    		while (2 * waitfor.size() >= acceptors.length) {
    			PaxosMessage msg = getNextMessage();
    
    			if (msg instanceof P2bMessage) {
    				P2bMessage m = (P2bMessage) msg;
    
    				if (ballot_number.equals(m.ballot_number)) {
    					if (waitfor.contains(m.src)) {
    						waitfor.remove(m.src);
    					}
    				}
    				else {
    					sendMessage(leader, new PreemptedMessage(me, m.ballot_number));
    					return;
    				}
    			}
    		}
	    } else {
	        //System.out.println("[" + me + "]RO:\t" + command.op.text());
	        P2aROMessage m2 = new P2aROMessage(me, ballot_number, slot_number, command);
	        for (ProcessId a: acceptors) {
	            sendMessage(a, m2);
	        }
	    }

		for (ProcessId r: replicas) {
			sendMessage(r, new DecisionMessage(me, slot_number, command));
		}
		sendMessage(leader, new DecisionUpdateMessage(me));
	}
}
