package Sim;

import java.util.Arrays;
import java.util.Comparator;

// An example of how to build a topology and starting the simulation engine

public class Run {
	public static void main (String [] args)
	{
 		//Creates two links
 		LossyLink link1 = new LossyLink(25, 10, 0.25);
 		LossyLink link2 = new LossyLink(0, 0, 0);
		
		// Create two end hosts that will be
		// communicating via the router
		Node host1 = new Node(1,1);
		Node host2 = new Node(2,1);

		//Connect links to hosts
		host1.setPeer(link1);
		host2.setPeer(link2);

		// Creates as router and connect
		// links to it. Information about 
		// the host connected to the other
		// side of the link is also provided
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(2);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);
		int packetsSentHostOne = 10;
		int packetsSentHostTwo = 0;
		// Generate some traffic
		// host1 will send 3 messages with time interval 5 to network 2, node 1. Sequence starts with number 1
		host1.StartSending(2, 2, packetsSentHostOne, 5, 1); 
		// host2 will send 2 messages with time interval 10 to network 1, node 1. Sequence starts with number 10
		host2.StartSending(1, 1, packetsSentHostTwo, 10, 101); 
		
		// Start the simulation engine and of we go!
		Thread t=new Thread(SimEngine.instance());
	
		t.start();
		try
		{
			t.join();
			/// Calculating statistics
			System.out.println("Packets dropped "+ LossyLink.dropPackets);
			System.out.println("Average A two B link1 " + ((float)link1.jitterA/(float)link1.numberOfPacketsSentByA));
			Object[] array =  link1.jitterAList.toArray();
			Arrays.sort(array);
			if((array.length%2)==0)
				System.out.println(array[(array.length-1)/2]+" "+array[(array.length)/2]);
			else {
				System.out.println(array[(array.length)/2]);
			}
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?" + e.getClass());
		}


	}
	
}
