package Sim;

public class BindingAck extends Message{
	
	
	public BindingAck(NetworkAddr from, NetworkAddr to, int seq) {
		super(from, to, seq);
	}

}
