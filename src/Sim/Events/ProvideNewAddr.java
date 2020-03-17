package Sim.Events;

import Sim.NetworkAddr;
import Sim.SimEnt;

public class ProvideNewAddr extends Message {

	/**
	 * 
	 * @param from
	 * @param to
	 * @param seq
	 */
	public ProvideNewAddr(NetworkAddr from, NetworkAddr to, int seq ) {
		super(from, to, seq);
		// TODO Auto-generated constructor stub
	}
	
	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}

}
