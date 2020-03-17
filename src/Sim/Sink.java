package Sim;

import Sim.Events.Message;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Sink extends Node {
	private NetworkAddr _id;
	private SimEnt _peer;

	public Sink (int network, int node)
	{
		super(network, node);
		_id = new NetworkAddr(network, node);
	}	
	
	
	// Sets the peer to communicate with. This node is single homed
	
	public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}
	
	
	public NetworkAddr getAddr()
	{
		return _id;
	}
		
//**********************************************************************************	
	
	// This method is called upon that an event destined for this node triggers.
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message)
		{
			System.out.println("Sink Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
			
		}
	}
}
