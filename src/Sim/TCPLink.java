package Sim;

import Sim.Events.TCPMessage;

public class TCPLink extends Link{
	int msgPerSecond;
	boolean canSendMessage;
	int msgs = 0;
	static float one = 1;
	static double transmitionTime = 0.02;
	
	public TCPLink(int msgPerSecond) {
		super();
		this.canSendMessage = true;
		this.msgPerSecond = msgPerSecond;
		
	}

	@Override
	public void recv(SimEnt src, Event ev)
	{
		if(ev instanceof TimerEvent)
		{
			msgs--;
			if(msgs< msgPerSecond)
				canSendMessage = true;
			return;
		}
		
		if(!canSendMessage)
		{
			System.out.println("Dropped packet with seq: " + ((TCPMessage)ev).seq() +" from " + src +   " because of not enough bandwidth");
			return;
		}
		
		if (src == _connectorA)
		{
			//System.out.println("Link recv msg, passes it through");
			send(_connectorB, ev, transmitionTime);
		}
		else if(_connectorA == _connectorB || _connectorA == null || _connectorB == null)
		{
			System.out.println("Link dropped packet");
		}
		else 
		{
			//System.out.println("Link recv msg, passes it through");
			send(_connectorA, ev, transmitionTime);
		}
		msgs++;
		if(msgs>=msgPerSecond)
			canSendMessage = false;
		send(this, new TimerEvent(), transmitionTime);
	}
}
