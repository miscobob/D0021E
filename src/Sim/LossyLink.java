package Sim;

import java.util.ArrayList;
import java.util.Random;

import Sim.Events.Message;
/**
 * A class intended to show the effects of a bad link in a network
 * @author Michael
 */
public class LossyLink  extends Link{
	public static int dropPackets = 0;
	private double dropChance;
	private int delay;
	private int jitter;
	private Random random;
	/**
	 * Constructor of a link that have a chance to drop packets and induces a delay with variation (jitter) 	
	 * @param delay static value a packet sent on link is delayed by
	 * @param jitter a dynamic value the packet is additionally delayed by, 0 to given value 
	 * @param dropChance chance the link has to drop a packet
	 */
	public LossyLink(int delay, int jitter, double dropChance) {
		this.delay = delay;
		this.dropChance = dropChance;
		this.jitter = jitter;
		this.random = new Random();
		
	}
	public ArrayList<Integer> jitterAList = new ArrayList<Integer>();
	public ArrayList<Integer> jitterBList = new ArrayList<Integer>();
	public int numberOfPacketsSentByA = 0;
	public int numberOfPacketsSentByB = 0;
	public int jitterA;
	public int lastDelayA;
	public int jitterB;
	private int lastDelayB; 
	@Override
	public void recv(SimEnt src, Event ev)
	{
		if (ev instanceof Message) {
			if(random.nextDouble() > dropChance) {/// checks if link should drop packet
				int nextDelay = random.nextInt(jitter+1) + delay; /// calculating how long the packet should delay by
				System.out.println("Link recv msg, passes it through, delayed " + nextDelay);
				if (src == _connectorA) {
					///for analysis
					numberOfPacketsSentByA++;
					int jitter = Math.abs(nextDelay-lastDelayA);
					jitterAList.add(jitter);
					jitterA += jitter;
					lastDelayA = nextDelay;
					///
					send(_connectorB, ev, nextDelay);
				}
				else
				{	
					///for analysis
					numberOfPacketsSentByB++;
					int jitter = Math.abs(nextDelay-lastDelayB);
					jitterBList.add(jitter);
					jitterB += jitter;
					lastDelayB = nextDelay;
					///
					send(_connectorA, ev, nextDelay);
				}
				}
			else {
				System.out.println("Bad link dropped packet with seq " + ((Message) ev).seq());
				dropPackets++;
			}
		
		}
		
	}
}
