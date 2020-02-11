package Sim;

import java.util.Arrays;

public class Run {
	public static void main (String [] args)
	{
 		Link link1 = new Link();
 		Link link2 = new Link();
		
		Node host1 = new Sink(1,1);
		Poisson host2 = new Poisson(2,1);

		host1.setPeer(link2);
		host2.setPeer(link2);
		
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(2);
		routeNode.connectInterface(0, link2, host1);
		routeNode.connectInterface(1, link2, host2);

		//Network, Node, Messages per second, Seconds to send
		host2.StartSending(1, 1, 3, 30000);
		
		Thread t=new Thread(SimEngine.instance());
	
		t.start();
		try
		{
			t.join();
		}
		catch (Exception e)
		{
			System.out.println("The motor seems to have a problem, time for service?" + e.getClass());
		}


	}
	
}
