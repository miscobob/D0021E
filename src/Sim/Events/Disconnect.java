package Sim.Events;

import Sim.Event;
import Sim.NetworkAddr;
import Sim.SimEnt;

public class Disconnect implements Event {
	private NetworkAddr _source; 
	
	public Disconnect(NetworkAddr source) {
		_source = source;
		
	}
	
	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}

	public NetworkAddr source() {
		// TODO Auto-generated method stub
		return _source;
	}
	
}
