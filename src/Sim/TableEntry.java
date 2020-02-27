package Sim;

// Just a class that works like a table entry hosting
// a link connecting and the node at the other end

public class TableEntry {

	NetworkAddr networkAddress;
	SimEnt link;
	
	TableEntry(NetworkAddr networkAddress, SimEnt link)
	{
		this.networkAddress = networkAddress;
		this.link = link;
	}
	
	protected SimEnt link()
	{
		return link;
	}
	
	protected int network()
	{
		return networkAddress.networkId();
	}

	protected int node()
	{
		return networkAddress.nodeId();
	}
	
}
