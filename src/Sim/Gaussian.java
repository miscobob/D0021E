package Sim;

import java.util.Random;
import java.lang.Math;

public class Gaussian extends Node {

	private NetworkAddr _id;
	private SimEnt _peer;
	private int _toNetwork = 0;
	private int _toHost = 0;
	
	private int _sentmsg=0;
	private int _seq = 0;
	
	private int _stdDeviation;
	private int _mean;
	private double _timeToSend;
	
	Random random = new Random();

    public Gaussian (int network, int node) {
        super(node, node);
        _id = new NetworkAddr(network, node);
    }

    // Modified to only take a time limit, diviation and mean.
    public void StartSendingNormal(int network, int node, int stdDeviation, int mean, double timeToSend)
    {
        _toNetwork = network;
        _stdDeviation = stdDeviation;
        _mean = mean;
        _toHost = node;
        _seq = 1;
        _timeToSend = timeToSend;
        send(this, new TimerEvent(), 0);
        System.out.println("Sending signal to start sending...");

    }
    
    public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}

    // Override: Modified to send packages as a normal distribution with a random gaussian number.
    public void recv(SimEnt src, Event ev)
    {
    	
        if (ev instanceof TimerEvent)
        {
            if (SimEngine.getTime() < _timeToSend)
            {
            	//The amount of packages to send this second
                int packages = (int)Math.abs(random.nextGaussian() * _stdDeviation + _mean);
                double _time = SimEngine.getTime();

                for (int i = 0; i < packages; i++)
                {
                    double x = Math.random();
                    double thisTime = SimEngine.getTime() + x;

                    try {
                    	
						Logger.LogTime("Gaussian_" + _timeToSend + "_timestamps.txt", Double.toString(thisTime));
					}
					catch (Exception e){
						System.out.println(e);
					}
                    
                    _sentmsg++;
                    //System.out.println(_peer);
                    send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), x);
                    System.out.println("Gaussian Traffic Generator Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+ thisTime);
                    _seq++;
                }
                try {
                	
					Logger.LogTime("Gaussian_" + _timeToSend + "_packages.txt", Double.toString(packages));
				}
				catch (Exception e){
					System.out.println(e);
				}
                
                send(this, new TimerEvent(),1);
            }
        }

        if (ev instanceof Message)
        {

            System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
        }
        
    }

}