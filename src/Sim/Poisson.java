package Sim;

import java.util.Random;

import Sim.Events.Message;

import java.lang.Math;

public class Poisson extends Node {

	private NetworkAddr _id;
	private SimEnt _peer;
	private int _toNetwork = 0;
	private int _toHost = 0;
	
	private int _sentmsg=0;
	private int _seq = 0;
	
	private double _lambda;
	private int _packages;
	
	Random random = new Random();

    public Poisson (int network, int node) {
        super(node, node);
        _id = new NetworkAddr(network, node);
    }

    public void StartSending(int network, int node, int mean, int packages)
    {
        _toNetwork = network;
        _lambda = Math.exp(-mean);
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

    //returns next time in milliseconds
    public int nextTime()
    {
    	int k = 0;
    	
    	for(double p = 1.0; p > _lambda; k++)
    	{
    		p *= random.nextDouble();
    	}
    	
    	return k - 1;
    }
    
    // Override: Modified to send packages as a normal distribution with a random gaussian number.
    public void recv(SimEnt src, Event ev)
    {
    	
        if (ev instanceof TimerEvent)
        {
        	if(_sentmsg > _packages)
        		return;
        	
        	double next = nextTime();
        	double nextSeconds = (next / 100.0);
        	
        	try {
            	
				Logger.LogTime("Poisson_" + _packages + "_timestamps_delta.txt", Double.toString(nextSeconds));
				Logger.LogTime("Poisson_" + _packages + "_timestamps.txt", Double.toString(SimEngine.getTime() + nextSeconds));
        	}
			catch (Exception e){
				System.out.println(e);
			}
        	_sentmsg++;
        	send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost), _seq), nextSeconds);
        	System.out.println("Gaussian Traffic Generator Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+ SimEngine.getTime());
            _seq++;	
        	
            send(this, new TimerEvent(), nextSeconds);
        	
        }

        if (ev instanceof Message)
        {

            System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
        }
        
    }

}