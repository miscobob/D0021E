package Sim;

import java.util.ArrayList;

public class TCPConnection 
{
	public enum threewayHandshakeStep
	{
		First, Second, Third, Complete
	}
	public enum fourwayHandshakeStep
	{
		First, Second, Third, Fourth, Complete
	}
	private int sequence;
	private int ack;
	private int duplicateAck = 0;
	private int packetSpeed;
	private threewayHandshakeStep ths = threewayHandshakeStep.First;
	private fourwayHandshakeStep fhs = null;
	private NetworkAddr correspondant;
	private NetworkAddr self;
	private double rtt;
	
	public TCPConnection(NetworkAddr correspondant, NetworkAddr self) 
	{
		this.correspondant = correspondant;
		this.self = self;
		sequence = 0;
		ack = 0;
		packetSpeed = 1;
	} 
	
	
	public TCPMessage reply(TCPMessage message) 
	{
		if(message.seq() == ack) 
		{
			ack++;
		}
		if(sequence < message.ack())
		{
			sequence = message.ack();
			duplicateAck = 1;
		}
		else if(sequence == message.ack()) 
		{
			duplicateAck++;
		}
		TCPType reply;
		if(ths != threewayHandshakeStep.Complete)
		{
			reply = OpeningConnectionStep(message.type());
		}
		else if(fhs != null) 
		{
			
		}
		else
			switch(message.type()) 
				{
				case ACK :
					break; 
				case SYN :
					break;
				case FIN :
					break;
				case SYNACK :
					break;
				case FINACK :
					break;
				default:
					break;
			}
		return null;
	}
	
	private TCPType OpeningConnectionStep(TCPType type) 
	{
		TCPType reply = null;
		switch(ths) 
		{
		case First:
			if(type == TCPType.SYN) 
			{
				reply = TCPType.SYNACK;
				ths = threewayHandshakeStep.Third;
			}
			break;
		case Second:
			if(type == TCPType.SYNACK) 
			{
				reply = TCPType.ACK;
				ths = threewayHandshakeStep.Complete;
			}
			break;
		case Third:
			if(type == TCPType.ACK)
				ths = threewayHandshakeStep.Complete;
			
			break;
		default:
			System.out.println("Failed in setting up the TCP to node " + correspondant);
			
		}	
		return reply;
	}
	
	public double getRTT() 
	{
		return rtt;
	}
	
	public void setRTT(double rtt) 
	{
		this.rtt = rtt;
	}
	
	public boolean connectionEstablished() 
	{
		return ths == threewayHandshakeStep.Complete && !(fhs != null);
	}
	
	public int getDuplicateAcks() 
	{
		return duplicateAck;
	}
	
	public int getSpeed() 
	{
		return packetSpeed;
	}
	
	public int ack() 
	{
		return ack;
	}
	
	public int seq() 
	{
		return sequence;
	}
	
	public NetworkAddr correspondant() 
	{
		return correspondant;
	}
}
