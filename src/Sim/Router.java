package Sim;


// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;
	
	// When created, number of interfaces are defined
	
	Router(int interfaces, int networkId)
	{
		
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
		setNetworkAddr(networkId, 0);
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if (interfaceNumber<_interfaces && _routingTable[interfaceNumber] == null)
		{
			_routingTable[interfaceNumber] = new RouteTableEntry(node._id, link);
		}
		else
			System.out.println("Trying to connect to port not in router");
		
		((Link) link).setConnector(this);
	}
	
	/**
	 * Return if could change interface
	 * @param node
	 * @param newInterface
	 * @return
	 */
	private boolean moveInterface(SimEnt node, int newInterface) {
		if(_routingTable[newInterface] != null) {
			return false;
		}
		Link link = (Link)removeFromInterface(node);
		Link newLink = new Link();
		newLink.setConnector(link._connectorA);
		newLink.setConnector(link._connectorB);
		link._connectorA = null;
		link._connectorB = null;
		((Node)node).setPeer(newLink);
		connectInterface(newInterface, newLink , node);
		return true;
	}
	
	/**
	 * Disconnects from link
	 * @param node which is removed
	 */
	private SimEnt removeFromInterface(SimEnt node) {
		SimEnt link = null;
		for(int i = 0; i<_routingTable.length; i++) 
		{
			if(_routingTable[i] != null)
			if(_routingTable[i].networkAddress.SameAddress(node._id))
			{
				link = _routingTable[i].link();
				_routingTable[i] = null;
				
				((Link) link).removeConnector(this);
			}
			
		}
		return link;
	}

	// This method searches for an entry in the routing table that matches
	// the network number in the destination field of a messages. The link
	// represents that network number is returned
	
	private SimEnt getInterface(NetworkAddr nAddr)
	{
		SimEnt routerInterface=null;
		if(nAddr.networkId() == _id.networkId()) {
			for(int i=0; i<_interfaces; i++)
				if (_routingTable[i] != null)
				{
					if (_routingTable[i].node() == nAddr.nodeId())
					{
						routerInterface = _routingTable[i].link();
					}
				}
		}
		else {
			for(int i=0; i<_interfaces; i++)
				if (_routingTable[i] != null)
				{
					if (_routingTable[i].network() == nAddr.networkId())
					{
						routerInterface = _routingTable[i].link();
					}
				}
		}
		return routerInterface;
	}
	
	
	// When messages are received at the router this method is called
	
	public void recv(SimEnt source, Event event)
	{
		if (event instanceof Message)
		{
			System.out.println("Router " + this.getAddr().networkId() + "." + this.getAddr().nodeId() +  " handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(((Message)  event).destination());
			System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());		
			send (sendNext, event, _now);
	
		}
		if(event instanceof Migrate) 
		{
			System.out.println("Router attempts to change interface for node " +((Migrate) event).source().getAddr().networkId() + " to  interface " +((Migrate) event).newInterface());
			((Migrate) event).isSuccess(moveInterface(((Migrate) event).source(),((Migrate) event).newInterface()));
			send(getInterface(((Migrate) event).source().getAddr()),event,0);
		}
	}
}
