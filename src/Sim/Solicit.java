package Sim;

public class Solicit implements Event {

	public NetworkAddr _source;
	public Solicit (NetworkAddr from)
	{
		_source = from;
	}
	
	@Override
	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}
}
