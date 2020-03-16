package Sim;

public class TCPLink extends Link{
	int msgPerSecond;
	boolean canSendMessage;

	public TCPLink(int msgPerSecond) {
		super();
		this.canSendMessage = true;
		this.msgPerSecond = msgPerSecond;
		
		send(this, new TimerEvent(),1000.0 / msgPerSecond);
	}

	@Override
	public void recv(SimEnt src, Event ev)
	{
		if(ev instanceof TimerEvent)
		{
			canSendMessage = true;
			return;
		}
		
		if(!canSendMessage)
		{
			System.out.println("Dropped packet with seq: " + ((TCPMessage)ev).seq() + " because of not enough bandwidth");
			return;
		}
		
		if (src == _connectorA)
		{
			//System.out.println("Link recv msg, passes it through");
			send(_connectorB, ev, _now);
		}
		else if(_connectorA == _connectorB || _connectorA == null || _connectorB == null)
		{
			System.out.println("Link dropped packet");
		}
		else 
		{
			//System.out.println("Link recv msg, passes it through");
			send(_connectorA, ev, _now);
		}
	}
}
