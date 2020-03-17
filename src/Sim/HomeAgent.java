package Sim;

import java.util.HashMap;

import Sim.Events.BindingAck;
import Sim.Events.BindingRequest;
import Sim.Events.ForwardMessage;
import Sim.Events.Message;

public class HomeAgent extends SimEnt{
	
	private HashMap<NetworkAddr, NetworkAddr> remoteNodes; //Remote address as value with key as localAddress
	private Router _router;
	
	public HomeAgent(Router router) {
		_router = router;
		this._id = router.getAddr();
		remoteNodes = new HashMap<NetworkAddr, NetworkAddr>();
		// TODO Auto-generated constructor stub
	}
	/**
	 * Checks if given nodeId is used in routing to a host on a remote network
	 * @param nodeId
	 * @return if nodeId in homeAgent table of remote Address or not
	 */
	public boolean inHomeAgent(int nodeId) {
		for(NetworkAddr addr: remoteNodes.keySet() ) {
			if(addr.nodeId() == nodeId) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasBeenConnected(NetworkAddr remoteAddr) {
		return remoteNodes.containsValue(remoteAddr);
	}
	
	public NetworkAddr getRemoteAddress(NetworkAddr localAddress) {
		return remoteNodes.get(localAddress);
	}
	
	@SuppressWarnings("unused")
	private void removeAddr(NetworkAddr remoteAddr) 
	{
		remoteNodes.remove(remoteAddr);
	}
	
	private void setRemoteNode(NetworkAddr localAddr, NetworkAddr remoteAddr) {
		remoteNodes.put(localAddr, remoteAddr);
	}
	
	
	@Override
	public void recv(SimEnt src, Event ev) 
	{
		
		if(ev instanceof BindingRequest) {
			BindingRequest br = (BindingRequest)ev;
			System.out.println("HomeAgent of "+ _router.toString() +" setting up new binding to node " + br.source() );
			setRemoteNode(br.localAddr(), br.source());
			send(_router, new BindingAck(_router.getAddr(), br.source(), 0), 0);
		}
		else if(remoteNodes.isEmpty()) {
			System.out.println("Homeagent dropped packet no host on foreign network");
		}
		else if(ev instanceof Message) 
		{
			Message msg = (Message)ev;
			System.out.println("Homeagent Forwarding message");
			
			send(_router, new ForwardMessage(this.getAddr(), getRemoteAddress(msg.destination()), msg.seq(), msg.source()), 0);
		}
	}
}
