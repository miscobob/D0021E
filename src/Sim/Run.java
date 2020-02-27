package Sim;

import java.util.Arrays;

public class Run {
	public static void main (String [] args)
	{
 		Link link1 = new Link();
 		Link link2 = new Link();
		
		Node host1 = new Node(1,1);
		Node host2 = new Node(2,1);

		host1.setPeer(link1);
		host2.setPeer(link2);
		
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(4, 0);
		routeNode.connectInterface(0, link1, host1);
		routeNode.connectInterface(1, link2, host2);
		
		host2.changeInterfaceAfter(10, 2);
		host1.changeInterfaceAfter(11, 2);
		
		//Network, Node, Messages per second, Seconds to send
		host2.StartSending(1, 1, 20, 1, 0);
		host1.StartSending(2, 2, 20, 1, 20);
		
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
