package Sim;

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
	public enum incrementStage 
	{
		Exponential, Constant
	}
	
	private int sequence;
	private int ack;
	private int duplicateAck = 0;
	private int congestionSize;
	private threewayHandshakeStep ths = threewayHandshakeStep.First;
	private fourwayHandshakeStep fhs = null;
	private NetworkAddr correspondant;
	private double rtt;
	private double srtt = -1;
	private int threshold = 32;
	private incrementStage stage = incrementStage.Exponential;
	
	
	public TCPConnection(NetworkAddr correspondant) 
	{
		this.correspondant = correspondant;
		sequence = 0;
		ack = 0;
		congestionSize = 1;
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