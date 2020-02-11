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
	private int _packages;
	
	Random random = new Random();

    public Gaussian (int network, int node) {
        super(node, node);
        _id = new NetworkAddr(network, node);
    }

    // Modified to only take a time limit, diviation and mean.
    public void StartSendingNormal(int network, int node, int stdDeviation, int mean, int packages)
    {
        _toNetwork = network;
        _stdDeviation = stdDeviation;
        _mean = mean;
        _toHost = node;
        _seq = 1;

        _packages = packages;
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
        	if(_sentmsg > _packages)
        		return;
        	
        	double x = random.nextGaussian() * _stdDeviation + _mean;
        	
        	try {
            	
				Logger.LogTime("Gaussian_" + _packages + "_timestamps_delta.txt", Double.toString(x));
				Logger.LogTime("Gaussian_" + _packages + "_timestamps.txt", Double.toString(x));
        	}
			catch (Exception e){
				System.out.println(e);
			}
        	_sentmsg++;
        	send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), x);
        	System.out.println("Gaussian Traffic Generator Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+ SimEngine.getTime());
            _seq++;	
        	
            send(this, new TimerEvent(), x);
        }

        if (ev instanceof Message)
        {

            System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
        }
        
    }

}