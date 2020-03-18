package Sim.Events;

import Sim.NetworkAddr;
import Sim.TCPType;

public class TCPMessage extends Message {
	
	private int ack;
	private TCPType type;
	private int data;
	private int segment = 0;
	private int segments = 0;
	private double timeStamp;
	private double ttl;
	
	

	public TCPMessage(NetworkAddr from, NetworkAddr to, int seq, int ack, TCPType type, int data) {
		super(from, to, seq);
		this.ack = ack;
		this.type = type;
		this.data = data;
	
	}
	
	public void setTTL(double ttl, double timeStamp) 
	{
		this.ttl = ttl;
		this.timeStamp = timeStamp;
	}
	
	public double getTimeout() 
	{
		return ttl+timeStamp;
	}
	
	public double getTTL() 
	{
		return ttl;
	}
	
	public double getTimeStamp() 
	{
		return timeStamp;
	}
	
	public void setSegment(int segment) 
	{
		this.segment = segment;
	}
	
	public void setSegments(int segments) 
	{
		this.segments = segments;
	}
	
	public int data() 
	{
		return data;
	}
	
	public int segment() 
	{
		return segment;
	}
	
	public int segments() 
	{
		return segments;
	}
	
	public int ack() {
		return ack;
	}
	
	public TCPType type() {
		return type;
	}
}
