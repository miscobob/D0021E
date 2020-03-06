package Sim;

public class TCPMessage extends Message {
	
	private int ack;
	private TCPType type;
	
	TCPMessage(NetworkAddr from, NetworkAddr to, int seq, int ack, TCPType type) {
		super(from, to, seq);
		this.ack = ack;
		this.type = type;
	}

	public int nextSequance() {
		return ack;
	}
	
	public TCPType type() {
		return type;
	}
}
