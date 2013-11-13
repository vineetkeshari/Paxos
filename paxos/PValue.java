package paxos;

public class PValue {
	BallotNumber ballot_number;
	int slot_number;
	Command command;

	public PValue(BallotNumber ballot_number, int slot_number,
											Command command){
		this.ballot_number = ballot_number;
		this.slot_number = slot_number;
		this.command = command;
	}
	
	public boolean equals(PValue other) {
	    return other.ballot_number.equals(ballot_number) &&
	            other.slot_number == slot_number &&
	            other.command.equals(command);
	}
	
	public int hashCode () {
	    return ballot_number.hashCode() + slot_number + command.hashCode();
	}

	public String toString(){
		return "PV(" + ballot_number + ", " + slot_number + ", " + command + ")";
	}
}
