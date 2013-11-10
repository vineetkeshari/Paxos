package paxos;

import java.util.*;

public class Leader extends Process {
    final long BASE_TIMEOUT = 100; // Nano-seconds
    long timeout = BASE_TIMEOUT;
    final double TIMEOUT_MULT = 1.1;
    final double TIMEOUT_NEG = BASE_TIMEOUT/100;
    
	ProcessId[] acceptors;
	ProcessId[] replicas;
	BallotNumber ballot_number;
	boolean active = false;
	Map<Integer, Command> proposals = new HashMap<Integer, Command>();
	
	Map<ProcessId, Long> leaderTimeouts = new HashMap<ProcessId, Long>();
	int next_round = 0;

	public Leader(Env env, ProcessId me, ProcessId[] acceptors,
										ProcessId[] replicas){
		this.env = env;
		this.me = me;
		ballot_number = new BallotNumber(0, me, timeout);
		this.acceptors = acceptors;
		this.replicas = replicas;
		env.addProc(me, this);
	}

	private void increaseTimeout() {
	    if (timeout <= 0)
	        timeout = BASE_TIMEOUT;
	    else
	        timeout *= TIMEOUT_MULT;
	}
	
	private void decreaseTimeout() {
	    timeout -=TIMEOUT_NEG;
	}
	
	private void waitFor (ProcessId otherLeader, int next_round, long ballot_timeout) {
        sendMessage (otherLeader, new PingMessage(me));
        leaderTimeouts.put(otherLeader, System.nanoTime() + ballot_timeout);
        if (next_round > this.next_round) {
            this.next_round = next_round;
        }
	}
    
    private void checkTimeouts() {
        for (ProcessId p : leaderTimeouts.keySet()) {
            if (timeout(p))
                break;
        }
    }
    
    private boolean timeout(ProcessId p) {
        if (System.nanoTime() > leaderTimeouts.get(p)) {
            leaderTimeouts.clear();
            advanceBallot();
            return true;
        } else {
            return false;
        }
    }
    
    private void advanceBallot() {
        if (next_round > ballot_number.round) {
            ballot_number = new BallotNumber(next_round, me, timeout);
        }
        new Scout(env, new ProcessId("scout:" + me + ":" + ballot_number),
            me, acceptors, ballot_number);
    }
	
	public void body(){
		System.out.println("Here I am: " + me);

		new Scout(env, new ProcessId("scout:" + me + ":" + ballot_number),
			me, acceptors, ballot_number);
		for (;;) {
		    checkTimeouts();
			PaxosMessage msg = getNextMessage();
			handle(msg);
		}
	}
	
	private void handle(PaxosMessage msg) {
	    
	    if (msg instanceof PingMessage) {
	        PingMessage m = (PingMessage) msg;
	        sendMessage (m.src, new PingResponseMessage(me, ballot_number));
	    }
	    
	    else if (msg instanceof PingResponseMessage) {
	        PingResponseMessage m = (PingResponseMessage) msg;
	        if (leaderTimeouts.containsKey(m.src) && !timeout (m.src)) 
	            waitFor (m.src, m.ballot_number.round + 1, m.ballot_number.timeout);
	    }
	    
	    else if (leaderTimeouts.isEmpty()) {
	        if (msg instanceof ProposeMessage) {
    			ProposeMessage m = (ProposeMessage) msg;
    			if (!proposals.containsKey(m.slot_number)) {
    				proposals.put(m.slot_number, m.command);
    				if (active) {
    					new Commander(env,
    						new ProcessId("commander:" + me + ":" + ballot_number + ":" + m.slot_number),
    						me, acceptors, replicas, ballot_number, m.slot_number, m.command);
    				}
    			}
    		}
    		
    		else if (msg instanceof AdoptedMessage) {
    			AdoptedMessage m = (AdoptedMessage) msg;
    
    			if (ballot_number.equals(m.ballot_number)) {
    	            decreaseTimeout();
    				Map<Integer, BallotNumber> max = new HashMap<Integer, BallotNumber>();
    				for (PValue pv : m.accepted) {
    					BallotNumber bn = max.get(pv.slot_number);
    					if (bn == null || bn.compareTo(pv.ballot_number) < 0) {
    						max.put(pv.slot_number, pv.ballot_number);
    						proposals.put(pv.slot_number, pv.command);
    					}
    				}
    
    				for (int sn : proposals.keySet()) {
    					new Commander(env,
    						new ProcessId("commander:" + me + ":" + ballot_number + ":" + sn),
    						me, acceptors, replicas, ballot_number, sn, proposals.get(sn));
    				}
    				active = true;
    			}
    		}
    
    		else if (msg instanceof PreemptedMessage) {
    			PreemptedMessage m = (PreemptedMessage) msg;
    			
    			if (ballot_number.compareTo(m.ballot_number) < 0) {
    	            increaseTimeout();
    	            waitFor (m.ballot_number.leader_id, m.ballot_number.round + 1, m.ballot_number.timeout);
    	            active = false;
    			}
    		}
    
    		else {
    			System.err.println("Leader: unknown msg type");
    		}
	    }
	}
	
}
