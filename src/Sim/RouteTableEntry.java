package Sim;

// This class represent a routing table entry by including
// the link connecting to an interface as well as the node 
// connected to the other side of the link

public class RouteTableEntry extends TableEntry{

	RouteTableEntry(NetworkAddr networkAddress, SimEnt link)
	{
		super(networkAddress, link);
	}
	
	public int network()
	{
		return super.network();
	}

	public int node()
	{
		return super.node();
	}
	
	public SimEnt Link()
	{
		return super.link();
	}
	
}
