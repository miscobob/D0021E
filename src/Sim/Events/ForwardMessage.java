package Sim.Events;

import Sim.NetworkAddr;

/**
 * A special type of Message sent from homeagent to intented receiver on other Network 
 * @author Michael MobilaDator
 *
 */
public class ForwardMessage extends Message{
	private NetworkAddr originalSource;
	public ForwardMessage(NetworkAddr from, NetworkAddr to, int seq, NetworkAddr originalSource) {
		super(from, to, seq);
		this.originalSource = originalSource;
	}
	
	public NetworkAddr getOriginalSource() {
		return originalSource;	
	}
	
	
}
