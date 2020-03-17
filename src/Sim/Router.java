package Sim;

import Sim.Events.Advertisement;
import Sim.Events.BindingRequest;
import Sim.Events.Disconnect;
import Sim.Events.Message;
import Sim.Events.Migrate;
import Sim.Events.Solicit;
import Sim.Events.UniqueAddr;

// This class implements a simple router

public class Router extends SimEnt{

	private RouteTableEntry [] _routingTable;
	private int _interfaces;
	private int _now=0;
	private HomeAgent _HA;
	
	// When created, number of interfaces are defined
	
	Router(int interfaces, int networkId)
	{
		
		_routingTable = new RouteTableEntry[interfaces];
		_interfaces=interfaces;
		setNetworkAddr(networkId, 0);
		_HA = new HomeAgent(this);
	}
	
	public void advertise()
	{
		System.out.println(this.toString() + " Sending advertisement to all connected interfaces:");
		for(RouteTableEntry addr : _routingTable)
		{
			if(addr == null)
				continue;

			System.out.println(this.toString() + " Sending advertisement to " + addr.networkAddress.toString());
			send (addr.link, new Advertisement(this._id, addr.networkAddress, 0), _now);
		}
	}
	
	public int getEmptyInterface() {
		
		for(int i = 0; i<_routingTable.length; i++) {
			if(_routingTable[i] == null) {
				return i;
			}
		}
		return -1;
	}
	
	// This method connects links to the router and also informs the 
	// router of the host connects to the other end of the link
	public void connectInterface(int interfaceNumber, SimEnt link, SimEnt node)
	{
		if(!uniqueAddress(node._id))
		{
			System.out.println("IP Address is not unique");
			return;
		}
		
		if (interfaceNumber<_interfaces && _routingTable[interfaceNumber] == null)
		{
			_routingTable[interfaceNumber] = new RouteTableEntry(node._id, link);
			System.out.println(node.toString() + " Successfully connected to " + this.toString());
		}
		else
		{
			System.out.println("Trying to connect to port not in router");
			return;
		}
		
		((Link) link).setConnector(this);
	}
	
	/**
	 * Checks whether the IP Address already exists in this router
	 * @param nA NetworkAddr to check
	 * @return true if address is unique, false if address is not
	 */
	private boolean uniqueAddress(NetworkAddr nA) {
		if(nA.nodeId() == 0 && nA.networkId() == getAddr().networkId())
			return false;
		for(RouteTableEntry addr : _routingTable)
		{
			if(addr == null)
				continue;
			
			if(addr.networkAddress.SameAddress(nA))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Return if could change interface
	 * @param networkAddr
	 * @param newInterface
	 * @return
	 */
	private Link moveInterface(NetworkAddr networkAddr, int newInterface) {
		if(_routingTable[newInterface] != null) {
			return null;
		}
		Link link = (Link)removeFromInterface(networkAddr);
		Link newLink = new Link();
		newLink.setConnector(link.getOther(this));
		newLink.setConnector(this);
		connectInterface(newInterface, newLink , link.getOther(this));
		link.removeConnector(link.getOther(this));
		link.removeConnector(this);
		
		return newLink;
	}
	
	/**
	 * Disconnects from link
	 * @param networkAddr which is removed
	 */
	private SimEnt removeFromInterface(NetworkAddr networkAddr) {
		SimEnt link = null;
		for(int i = 0; i<_routingTable.length; i++) 
		{
			if(_routingTable[i] != null)
			if(_routingTable[i].networkAddress.SameAddress(networkAddr))
			{
				link = _routingTable[i].link();
				_routingTable[i] = null;
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
			if(routerInterface == null)
				routerInterface = _HA;
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
		if(event instanceof Migrate) 
		{
			System.out.println("Router attempts to change interface for node " +((Migrate) event).source().networkId() + " to  interface " +((Migrate) event).newInterface());
			((Migrate) event).newLink(moveInterface(((Migrate) event).source(),((Migrate) event).newInterface()));
			send(getInterface(((Migrate) event).source()),event,0);
		}
		else if(event instanceof Disconnect) {
			Link link = (Link) removeFromInterface(((Disconnect) event).source());
			link.removeConnector(this);
		}
		else if(event instanceof UniqueAddr) {
			System.out.println(toString() +" handles a request to test if address is unique");
			((UniqueAddr)event).setUnique(uniqueAddress(((UniqueAddr)event).getAddr()));
			send(source, event, 0);
		}
		else if(event instanceof Solicit)
		{
			System.out.println(this.toString() + " Received Solicitation request from Node " + ((Solicit)event).source());
			advertise();
		}
		else if(event instanceof Advertisement)
		{
			System.out.println(this.toString() + " Received Advertisement from Router " + ((Advertisement)event).source());
		}
		else if(event instanceof BindingRequest) {
			BindingRequest br = (BindingRequest) event;
			if(br.destination().SameAddress(getAddr())) {
				System.out.println(this.toString()+ " received a binding request");
				send(_HA, br, 0);
			}
			else{
				System.out.println("Router " + this.getAddr().networkId() + "." + this.getAddr().nodeId() +  " handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
				SimEnt sendNext = getInterface(br.destination());
				System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());	
				send (sendNext, event, _now);
			}
			
		}
		else if (event instanceof Message)
		{
			System.out.println("Router " + this.getAddr().networkId() + "." + this.getAddr().nodeId() +  " handles packet with seq: " + ((Message) event).seq()+" from node: "+((Message) event).source().networkId()+"." + ((Message) event).source().nodeId() );
			SimEnt sendNext = getInterface(((Message)  event).destination());
			System.out.println("Router sends to node: " + ((Message) event).destination().networkId()+"." + ((Message) event).destination().nodeId());		
			send (sendNext, event, _now);
		}
	}
}
