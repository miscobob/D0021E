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
	private fourwayHandshakeStep fhs = fourwayHandshakeStep.First;
	private boolean startClosing = false;
	private NetworkAddr correspondant;
	private NetworkAddr self;
	
	public TCPConnection(NetworkAddr correspondant, NetworkAddr self) 
	{
		this.correspondant = correspondant;
		this.self = self;
		sequence = 0;
		ack = 0;
	} 
	
	
	public TCPMessage reply(TCPMessage message) 
	{
		if(message.seq() == ack) 
		{
			ack++;
		}
		else 
		{
			
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
			switch(ths) 
			{
			case First:
				if(message.type() == TCPType.SYN) 
				{
					reply = TCPType.SYNACK;
					ths = threewayHandshakeStep.Third;
				}
				break;
			case Second:
				if(message.type() == TCPType.SYNACK) 
				{
					reply = TCPType.ACK;
					ths = threewayHandshakeStep.Complete;
				}
				break;
			case Third:
				if(message.type() == TCPType.ACK)
					ths = threewayHandshakeStep.Complete;
				break;
			default:
				System.out.println("Failed in setting up the TCP to node " + correspondant);
				break;
			}
		else if(startClosing) 
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
