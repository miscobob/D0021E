package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class Migrate implements Event{
	private NetworkAddr _source;
	private int _newInterface;
	private Link newLink;
	private boolean _success = false;
	public Migrate (NetworkAddr from, int newInterface)
	{
		_source = from;
		_newInterface = newInterface;
	}
	
	public void newLink(Link link) {
		newLink = link;
	}
	
	public int newInterface() 
	{
		return _newInterface;
	}
	
	public NetworkAddr source()
	{
		return _source; 
	}
	public Link getNewLink() 
	{
		return newLink;
	}

	public void entering(SimEnt locale)
	{
	}
}
	
