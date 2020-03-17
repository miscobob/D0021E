package Sim;

import java.util.HashMap;

public class TCPConnection 
{
	public enum threewayHandshakeStep
	{
		First, Second, Third, Complete
	}
	public enum fourwayHandshakeStep
	{
		First, Second, Third, Complete
	}
	public enum incrementStage 
	{
		Exponential, Constant
	}
	
	private int sequence;
	private int ack;
	private int duplicateAck = 0;
	private int congestionSize;
	private int closeCondition;
	private threewayHandshakeStep ths = threewayHandshakeStep.First;
	private fourwayHandshakeStep fhs = null;
	private NetworkAddr correspondant;
	private NetworkAddr self;
	private double rtt;
	private double srtt = -1;
	private int threshold = 32;
	private incrementStage stage = incrementStage.Exponential;
	private HashMap<Integer,TCPMessage>messages;
	public final static int noCloseCodition = -1;
	
	
	public TCPConnection(NetworkAddr correspondant, NetworkAddr self, int closeCondition) 
	{
		this.correspondant = correspondant;
		this.self = self;
		sequence = 0;
		ack = 0;
		congestionSize = 1;
		this.closeCondition = closeCondition;
		messages = new HashMap<Integer, TCPMessage>();
	} 
	
	public TCPMessage openingConnectionMessage() 
	{
		return new TCPMessage(self, correspondant, sequence, ack, TCPType.SYN);
	}
	
	public TCPMessage nextMessage() 
	{
		TCPMessage msg = new TCPMessage(self, correspondant, sequence, ack, null);
		sequence++;
		return closeCondition != noCloseCodition && ths == threewayHandshakeStep.Complete && fhs == null ? msg : null;
	}
	
	public TCPMessage reply(TCPMessage message) 
	{
		
		if(message.seq() == ack) 
		{
			messages.remove(ack);
			ack++;
		}
		else if(message.seq() < ack) 
		{
			return null;
		}
		else 
		{
			if(message.seq() > ack)
				messages.remove(message.seq());
			return messages.get(ack);
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
		TCPType reply = null;
		if(ths != threewayHandshakeStep.Complete)
		{
			System.out.println(this.self.toString() + " Handling opening three-way handshake with " + message.source().toString());
			reply = OpeningConnectionStep(message.type());
			System.out.println(this.self.toString() + " Currently at step " + this.ths.toString());
		}
		else if(fhs != null) 
		{
			System.out.println(this.self.toString() + " Handling closing four-way handshake with " + message.source().toString());
			reply = closingConnectionStep(message.type());
			System.out.println(this.self.toString() + " Currently at step " + this.fhs.toString());
		}
		else
			switch(message.type()) 
				{
				case ACK :
					System.out.println("Received ACK when it was expected");
					if(ack >= closeCondition)
						reply = TCPType.FIN;
					else
						return null;
					break;
				case SYN :
					System.out.println("Received SYN when it was not expected");
					return null;
				case FIN :
					fhs = fourwayHandshakeStep.First;
					reply = closingConnectionStep(message.type());
					break;
				case SYNACK :
					System.out.println("Received SYNACK when it was not expected");
					return null;
				case FINACK :
					System.out.println("Received FINACK when it was not expected");
					return null;
				default:
					reply = TCPType.ACK;
					System.out.println("Received TCPMessage to return data");
					break;
			}
		TCPMessage msgToSend = new TCPMessage(self, correspondant, sequence, ack, reply);
		messages.put(ack, msgToSend);
		return msgToSend;
		
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
	

	private TCPType closingConnectionStep(TCPType type) //maybe?
	{
		TCPType reply = null;
		switch(fhs) 
		{
		case First:
			reply = TCPType.FINACK;
			fhs = fourwayHandshakeStep.Third;
			
			break;
		case Second:
			if(type == TCPType.FINACK) 
			{
				reply = TCPType.ACK;
				fhs = fourwayHandshakeStep.Complete;
			}
			break;
		case Third:
			if(type == TCPType.ACK)
				fhs = fourwayHandshakeStep.Complete;
			
			break;
		default:
			if(type == TCPType.FIN)
				fhs = fourwayHandshakeStep.Second;
			break;
			
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
		if(srtt == -1)
			srtt = rtt;
	}
	
	public double calculateSRTT()
	{
		double alpha = 0.8; //between 0.8 and 0.9
		
		double value = (alpha * srtt) + ((1 - alpha) * rtt);
		
		srtt = value;
				
		return value;
	}
	
	public double getRTO()
	{
		double beta = 1.3; //between 1.3 and 2.0
		return Math.min(64, Math.max(1, (beta * srtt)));
	}
	
	public boolean timedOut() //implement at some point idk fam
	{
		return false;
	}
	
	public boolean connectionEstablished() 
	{
		return ths == threewayHandshakeStep.Complete && fhs == null;
	}
	
	public int getDuplicateAcks() 
	{
		return duplicateAck;
	}
	
	public int getCongestionSize() 
	{
		return congestionSize;
	}
	
	public void setCongestionSize(int size) {
		congestionSize = size;
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
	
	public int getThreshold() {
		return threshold;
	}
	public void setIncrementStage(incrementStage _stage) {
		stage = _stage;
	}
	
	public incrementStage getIncrementStage() {
		return stage;
	}
	
}
