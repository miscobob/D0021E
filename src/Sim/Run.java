package Sim;

public class Run {
	public static void main (String [] args)
	{
 		Link link1 = new Link();
 		Link link2 = new Link();
 		Link link3 = new Link();
		
		Node host1 = new Node(0,1);
		Node host2 = new Node(1,1);

		host1.setPeer(link1);
		host2.setPeer(link2);
		
		
		// Note. A switch is created in same way using the Switch class
		Router routeNode = new Router(5, 0);
		Router routeNode2 = new Router(5, 1);
		
		//connect router to each other
		routeNode.connectInterface(0, link3, routeNode2);
		routeNode2.connectInterface(0, link3, routeNode);
		
		routeNode.connectInterface(1, link1, host1);
		routeNode2.connectInterface(1, link2, host2);
		
		//host2.changeInterfaceAfter(10, 2);
		//host1.changeInterfaceAfter(11, 2);
		
		//Network, Node, Messages per second, Seconds to send
		//host2.StartSending(0, 1, 10, 1, 0);
		//host1.StartSending(1, 1, 10, 1, 10);
		//host1.changeRouterAfter(5, routeNode2);
		//host2.sendSolicitationRequest();
		host1.startTCPConnection(host2._id);
		
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
