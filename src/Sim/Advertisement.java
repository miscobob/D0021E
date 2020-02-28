package Sim;

public class Advertisement implements Event {

	public NetworkAddr _source;
	public Advertisement (NetworkAddr from)
	{
		_source = from;
	}
	
	@Override
	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}
}
