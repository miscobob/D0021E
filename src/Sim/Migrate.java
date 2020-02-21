package Sim;

// This class implements an event that send a Message, currently the only
// fields in the message are who the sender is, the destination and a sequence 
// number

public class Migrate implements Event{
	private Node _source;
	private int _newInterface;
	private boolean _success = false;
	public Migrate (Node from, int newInterface)
	{
		_source = from;
		_newInterface = newInterface;
	}
	
	public void isSuccess(boolean success) {
		_success = success;
	}
	
	public int newInterface() 
	{
		return _newInterface;
	}
	
	public Node source()
	{
		return _source; 
	}
	
	public boolean success() {
		return _success;
	}

	public void entering(SimEnt locale)
	{
	}
}
	
