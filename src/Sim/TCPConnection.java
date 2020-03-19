package Sim;

import Sim.Events.TCPMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class TCPConnection extends SimEnt{
	public enum Config
	{
		Sender, Receiver
	}
	public enum IncrementStage
	{
		Constant, Exponential
	}
	public enum ConnectionStage
	{
		Opening, Open, HalfClosed, Closed;
	}
	private Config config;
	private ConnectionStage stage;
	private int seq;
	private int lastAck;
	private int nextWantedSeq = -1;
	private int duplicateAcks;
	private int dataToFetch;
	private double ttl = 10;
	private double rtt;
	private double srtt;
	private double congestionSize;
	private double threshold;
	private TCPType waitingOn;
	private IncrementStage incStage;
	private HashMap<Integer, TCPMessage>waitingOnAck;/// seq, TCPMessage
	private TCPQueue toSend;
	private Node self;
	private NetworkAddr correspondant;
	
	public TCPConnection(Config config, Node self, NetworkAddr correspondant) 
	{
		seq = 0;
		waitingOnAck = new HashMap<Integer,TCPMessage>();
		this.config = config;
		toSend = new TCPQueue();
		this.self = self;
		this.correspondant = correspondant;
		congestionSize = 1;
		incStage = IncrementStage.Exponential;
		stage = ConnectionStage.Opening;
		if(config == Config.Sender)
			waitingOn = TCPType.SYN;
		else
			waitingOn = TCPType.SYNACK;
	}
	
	public void startConversation() 
	{
		TCPMessage msg = new TCPMessage(self.getAddr(), correspondant, seq, -1,TCPType.SYN, 0);
		waitingOn = TCPType.SYNACK;
		seq++;
		toSend.addToHead(msg);
		send(this,new TimerEvent(), 0);
	}
	
	private TCPMessage getNextMessage() 
	{
		for(TCPMessage msg : waitingOnAck.values()) 
		{
			if(SimEngine.getTime()>msg.getTimeout()) 
			{
				timeout();
				return msg;
			}
		}
		if(!toSend.isEmpty()) 
		{
			TCPMessage msg = toSend.getHead();
			if(msg.type() == TCPType.SYN) 
				waitingOn = TCPType.SYNACK;
			
			else if(msg.type() == TCPType.FIN) 
			{
				stage = ConnectionStage.HalfClosed;
				waitingOn = TCPType.FINACK;
			}
			else if(msg.type() == TCPType.FINACK || msg.type() == TCPType.SYNACK) 
			{
				if(stage == ConnectionStage.Open)
					stage = ConnectionStage.HalfClosed;
				waitingOn = TCPType.ACK;
			}
			else if(stage == ConnectionStage.HalfClosed && msg.type() == TCPType.ACK) 
			{
				stage = ConnectionStage.Closed;
			}
				
			return msg;
		}return null;
	}
	
	public void setDataToFetch(int dataToFetch) 
	{
		this.dataToFetch = dataToFetch;
	}
	private int[]seg = new int[100];
	public void handleMessage(TCPMessage msg) 
	{
		recv++;
		System.out.println(self + " handling tcp message " + msg.type() + " from " + correspondant);
		if(waitingOn == null) 
		{
			if(config == Config.Sender) 
			{
				messageHandlerSender(msg);
			}
			else if(config == Config.Receiver)
			{
				messageHandlerReceiver(msg);
			}
		}
		else if(waitingOn == msg.type())
		{
			TCPMessage reply;
			switch(waitingOn) 
			{
			case SYN:
				reply = new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.SYNACK, 0);
				toSend.addToHead(reply);
				seq++;
				waitingOn = TCPType.ACK;
				send(this,new TimerEvent(), 0);
				break;
			case SYNACK:
				reply = new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.ACK, 0);
				waitingOnAck.remove(msg.ack());
				toSend.addToHead(reply);
				stage = ConnectionStage.Open;
				seq++;
				waitingOn = null;
				break;
			case ACK:
				waitingOnAck.remove(msg.ack());
				if(stage == ConnectionStage.Opening) 
					stage = ConnectionStage.Open;
				else if(stage == ConnectionStage.HalfClosed)
					stage = ConnectionStage.Closed;
				waitingOn = null;
				break;
			case FINACK:
				reply = new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.ACK, 0);
				waitingOnAck.remove(msg.ack());
				toSend.addToHead(reply);
				seq++;
				waitingOn = null;
				break;
			default:
				System.out.println("Something went wrong in " + self + " communcatining with " + correspondant );
				
			}
			if(stage == ConnectionStage.Open && dataToFetch > 0) 
			{
				toSend.addToTail(new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.ACK, dataToFetch));
				seq++;
			}
			
		}
	}

	private void messageHandlerReceiver(TCPMessage msg) {
		if(msg.type() == TCPType.ACK) {
			 if(msg.segments() != 0 && msg.segments() >= msg.segment()) 
			{
				 seg[msg.segment()-1] = 1;
				 if(nextWantedSeq == -1)
					nextWantedSeq = msg.seq()-msg.segment()+1;
				 nextWantedSeq = msg.seq() == nextWantedSeq ? nextWantedSeq+1 : nextWantedSeq;
				 TCPMessage reply = new TCPMessage(self.getAddr(), correspondant, seq, nextWantedSeq, TCPType.ACK, 0);
				 seq++;
				 toSend.addToTail(reply);
				 if(msg.segment() == msg.segments() && msg.seq() + 1 == nextWantedSeq)
				 {
					 TCPMessage fin = new TCPMessage(self.getAddr(), correspondant, seq, nextWantedSeq, TCPType.FIN, 0);
					 seq++;
					 toSend.addToTail(fin);
				 }
			}
			else 
			{	
				if(msg.ack() == lastAck) 
				{
					duplicateAcks++;
				}
				else 
				{
					lastAck = msg.ack();
					waitingOnAck.remove(msg.ack());
					duplicateAcks = 1;
				}
			}
		}
		else if(msg.type() == TCPType.FIN) 
		{
			TCPMessage reply = new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.FINACK, 0);
			seq++;
			toSend.addToTail(reply);
		}
	}

	private void messageHandlerSender(TCPMessage msg) {
		if(msg.data() > 0 && msg.type() == TCPType.ACK) 
		{
			int segments = msg.data();
			for(int segment = 1; segment <= segments; segment++) 
			{
				TCPMessage reply = new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.ACK, 0);
				reply.setSegment(segment);
				reply.setSegments(segments);
				toSend.addToTail(reply);
				seq++;
			}
		}
		else if(msg.type() == TCPType.ACK) 
		{
			if(msg.ack() == lastAck) 
			{
				duplicateAcks++;
				if (duplicateAcks>3) {
					threeDupAck();
					duplicateAcks = 0;
					toSend.addToHead(waitingOnAck.get(msg.ack()+1));
				}
			}
			else 
			{
				lastAck = msg.ack();
				waitingOnAck.remove(msg.ack());
				duplicateAcks = 1;
			}
		}
		else if(msg.type() == TCPType.FIN) 
		{
			TCPMessage reply = new TCPMessage(self.getAddr(), correspondant, seq, msg.seq()+1, TCPType.FINACK, 0);
			seq++;
			toSend.addToTail(reply);
		}
	}
	
	public NetworkAddr correspondant() 
	{
		return correspondant;
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
	
	public void threeDupAck() 
	{
		congestionSize = (int)Math.ceil(congestionSize/2.0);
		incStage = TCPConnection.IncrementStage.Constant;
		if (congestionSize < 2) {
			congestionSize = (2);
		}
	}
	
	public void timeout() 
	{
		congestionSize = 1;
		incStage = TCPConnection.IncrementStage.Exponential;
	}
	
	public void reachedThreshold() 
	{
		incStage = TCPConnection.IncrementStage.Constant;
	}

	private void updatingSendingRate() 
	{
		if(incStage == IncrementStage.Constant)
			congestionSize++;
		else {
			congestionSize = congestionSize * 2;
			if(congestionSize >= threshold)
				reachedThreshold();
		}
	}
	private int sent = 0;
	private int recv = 0;
	
	@Override
	public void recv(SimEnt source, Event event) {
		if(event instanceof TimerEvent) 
		{
			if(stage != ConnectionStage.Closed) 
			{
				TCPMessage msg = getNextMessage();
				if(msg != null)
				{
					System.out.println(self + " sends tcp message " + msg.type() +" to "+ correspondant);
					if((config == Config.Sender && stage == ConnectionStage.Open && msg.type() == TCPType.ACK)|| waitingOn != null)
						waitingOnAck.put(msg.seq()+1, msg);
					msg.setTTL(ttl, SimEngine.getTime());
					self.sendTCP(msg, 1/congestionSize);
					try {
						Logger.LogTime(self.toString(), Double.toString(congestionSize));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sent++;
					updatingSendingRate();
				}
				send(this, new TimerEvent(), 1/congestionSize);
			}
			else 
			{
				System.out.println("Communcation has ended sent "+ sent + " and received "+ recv );
			}
		}
		
	}
	
	
}
