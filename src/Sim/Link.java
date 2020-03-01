package Sim;

// This class implements a link without any loss, jitter or delay

public class Link extends SimEnt{
	protected SimEnt _connectorA=null;
	protected SimEnt _connectorB=null;
	private int _now=0;
	
	public Link()
	{
		super();	
	}
	
	// Connects the link to some simulation entity like
	// a node, switch, router etc.
	
	public void setConnector(SimEnt connectTo)
	{
		if (_connectorA == null) 
			_connectorA=connectTo;
		else
			_connectorB=connectTo;
	}
	
	public void removeConnector(SimEnt connected) {
		if (_connectorA == connected) 
			_connectorA=null;
		else if(_connectorB == connected)
			_connectorB=null;
	}
	
	public SimEnt getOther(SimEnt ent) {
		if(ent == _connectorA)
			return _connectorB;
		else
			return _connectorA;
	}

	// Called when a message enters the link
	
	public void recv(SimEnt src, Event ev)
	{
			if (src == _connectorA)
			{
				//System.out.println("Link recv msg, passes it through");
				send(_connectorB, ev, _now);
			}
			else if(_connectorA == _connectorB || _connectorA == null || _connectorB == null)
			{
				System.out.println("Link dropped packet");
			}
			else {
				//System.out.println("Link recv msg, passes it through");
				send(_connectorA, ev, _now);
			}
	}	
}