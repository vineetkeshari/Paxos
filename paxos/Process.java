package paxos;

public abstract class Process extends Thread {
    private static final long DELAY = 100; // Milliseconds
    
	ProcessId me;
	Queue<PaxosMessage> inbox = new Queue<PaxosMessage>();
	Env env;

	abstract void body();

	public void run(){
		body();
		env.removeProc(me);
	}

	PaxosMessage getNextMessage(){
		return inbox.bdequeue();
	}

	void sendMessage(ProcessId dst, PaxosMessage msg){
	    //System.out.println("[" + System.nanoTime() + "]\tSEND\t" + me + "\t" + dst + "\t" + msg);
		env.sendMessage(dst, msg);
		try {
		    Thread.sleep(DELAY);
		} catch (InterruptedException e) {
		    System.out.println("Delay failed!");
		}
	}

	void deliver(PaxosMessage msg){
		inbox.enqueue(msg);
	}
}
