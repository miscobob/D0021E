package Sim;

// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class CBR extends Node {
	private NetworkAddr _id;
	private SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;
	

	private int _toNetwork = 0;
	private int _toHost = 0;
	
	private int _pkgPerSecond;
	private int _timeToSend;

	
	public CBR (int network, int node)
	{
		super(network, node);
		_id = new NetworkAddr(network, node);
	}	
	
	public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}
	
	public NetworkAddr getAddr()
	{
		return _id;
	}

	public void StartSending(int network, int node, int pkgPerSecond, int timeToSend)
	{
		_toNetwork = network;
		_toHost = node;
		_pkgPerSecond = pkgPerSecond;
		_timeToSend = timeToSend;
		send(this, new TimerEvent(), 0);
	}
	
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof TimerEvent)
		{	
			if (SimEngine.getTime() < _timeToSend)
			{
				//used for logging timestamps
				double _time = SimEngine.getTime();
				
				for (int i = 0; i < _pkgPerSecond; i++)
				{
					try {
	
						Logger.LogTime("CBR_" + _pkgPerSecond * _timeToSend + "_timestamps.txt", Double.toString(_time));
					}
					catch (Exception e){
						System.out.println(e);
					}
					
					_sentmsg++;
					send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
					System.out.println("Traffic Generator CBR Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
					_seq++;
					_time += 1.0 / _pkgPerSecond;
				}	
			}
			send(this, new TimerEvent(),1);
		}
		if (ev instanceof Message)
		{
			System.out.println("Traffic Generator CBR Node "+_id.networkId()+ "." + _id.nodeId() +" received message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
		}
	}
}
