package Sim;

import java.util.Arrays;

public class Run {
	public static void main (String [] args)
	{
 		LossyLink link1 = new LossyLink(25, 10, 0.25);
 		LossyLink link2 = new LossyLink(0, 0, 0);
		
		Node host1 = new Sink(1,1);
		CBR host2 = new CBR(2,1);

		host1.setPeer(link2);
		host2.setPeer(link2);
		
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(2);
		routeNode.connectInterface(0, link2, host1);
		routeNode.connectInterface(1, link2, host2);

		//Network, Node, Messages per second, Seconds to send
		host2.StartSending(1, 1, 2, 10);
		
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
