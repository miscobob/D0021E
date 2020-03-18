package Sim;

import Sim.Events.TCPMessage;

import java.util.ArrayList;
import java.util.HashMap;

public class TCPConnection {
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
	private int nextWantedSeq;
	private ArrayList<Integer> ackMsgs;
	private int duplicateAcks;
	private int rtt;
	private float congestionSize;
	private float threshold;
	private TCPType waitingOn;
	private IncrementStage incStage;
	private HashMap<Integer, TCPMessage>waitingOnAck;/// seq, TCPMessage
	private TCPQueue toSend;
	private NetworkAddr self;
	private NetworkAddr correspondant;
	
	public TCPConnection(Config config, NetworkAddr self, NetworkAddr correspondant) 
	{
		seq = 0;
		waitingOnAck = new HashMap<Integer,TCPMessage>();
		this.config = config;
		toSend = new TCPQueue();
		this.self = self;
		this.correspondant = correspondant;
		incStage = IncrementStage.Exponential;
		stage = ConnectionStage.Opening;
		waitingOn = TCPType.SYN;
		ackMsgs = new ArrayList<Integer>();
	}
	
	public TCPMessage getFirstMessage() 
	{
		TCPMessage msg = new TCPMessage(self, correspondant, seq, -1,TCPType.SYN, 0);
		waitingOn = TCPType.SYNACK;
		seq++;
		return msg;
	}
	
	public TCPMessage getNextMessage() 
	{
		if(toSend.isEmpty()) 
		{
			TCPMessage msg = toSend.getHead();
			if(msg.type() == TCPType.FIN || msg.type() == TCPType.FINACK || msg.type() == TCPType.SYNACK) 
			{
				if(stage == ConnectionStage.Open)
					stage = ConnectionStage.HalfClosed;
				waitingOn = TCPType.ACK;
			}
			return msg;
		}return null;
	}
	
	
	public void handleMsg(TCPMessage msg) 
	{
		if(waitingOn == null) 
		{
			if(config == Config.Sender) 
			{
				if(msg.data() > 0 && msg.type() == TCPType.ACK) 
				{
					int segments = msg.data();
					for(int segment = 1; segment <= segments; segment++) 
					{
						TCPMessage reply = new TCPMessage(self, correspondant, seq, msg.seq()+1, TCPType.ACK, 0);
						seq++;
						reply.setSegment(segment);
						reply.setSegments(segments);
						toSend.addToTail(reply);
					}
				}
				else if(msg.type() == TCPType.ACK) 
				{
					if(msg.ack() == lastAck)
						duplicateAcks++;
					else 
					{
						lastAck = msg.ack();
						waitingOnAck.remove(msg.ack());
						duplicateAcks = 1;
					}
				}
				else if(msg.type() == TCPType.FIN) 
				{
					TCPMessage reply = new TCPMessage(self, correspondant, seq, msg.seq()+1, TCPType.FINACK, 0);
					seq++;
				}
			}
			else if(config == Config.Receiver)
			{
				if(msg.type() == TCPType.ACK) {
					 if(msg.segments() != 0 && msg.segments() >= msg.segment()) 
					{
						
						nextWantedSeq = msg.seq() == nextWantedSeq || m
						TCPMessage reply;
					}
					else 
					{	
						if(msg.ack() == lastAck)
							duplicateAcks++;
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
					TCPMessage reply = new TCPMessage(self, correspondant, seq, msg.seq()+1, TCPType.FINACK, 0);
					seq++;
				}
				
			}
		}
		else 
		{
			TCPMessage reply;
			switch(waitingOn) 
			{
			case SYN:
				reply = new TCPMessage(self, correspondant, seq, msg.seq()+1, TCPType.SYNACK, 0);
				seq++;
				waitingOn = TCPType.ACK;
				break;
			case SYNACK:
				reply = new TCPMessage(self, correspondant, seq, msg.seq()+1, TCPType.ACK, 0);
				seq++;
				waitingOn = null;
				break;
			case ACK:
				waitingOn = null;
				break;
			case FINACK:
				reply = new TCPMessage(self, correspondant, seq, msg.seq()+1, TCPType.ACK, 0);
				seq++;
				waitingOn = null;
				
			}
			
		}
	}
	
	/*
	private void reno(TCPConnection con) {
		if (con.getDuplicateAcks() >= 3) {
			con.setCongestionSize((int)Math.ceil(con.getCongestionSize()/2.0));
			con.setIncrementStage(TCPConnection.incrementStage.Constant);
			if (con.getCongestionSize() < 2) {
				con.setCongestionSize(2);
			}
		}
		else if (con.timedOut()) {
			con.setCongestionSize(1);
			con.setIncrementStage(TCPConnection.incrementStage.Exponential);
		}
		else if (con.getCongestionSize() >= con.getThreshold()) {
			con.setIncrementStage(TCPConnection.incrementStage.Constant);
		}
	}
*/
}
