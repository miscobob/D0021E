package Sim.Events;

import Sim.NetworkAddr;

public class BindingRequest extends Message {

	private NetworkAddr _localAddr;

	public BindingRequest(NetworkAddr from, NetworkAddr to, int seq , NetworkAddr localAddr) {
		super(from, to, seq);
		_localAddr = localAddr;
		// TODO Auto-generated constructor stub
	}
	public NetworkAddr localAddr() {
		return _localAddr;
	}

	
}
