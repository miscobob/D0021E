package Sim;

import Sim.Events.TCPMessage;

public class TCPQueue{
	
	public class QueueElement
	{
		QueueElement next = null;
		TCPMessage msg;
		public QueueElement(TCPMessage msg) 
		{
			this.msg = msg;
		}
	}
	
	private QueueElement first;
	
	public void addToTail(TCPMessage msg) 
	{
		if(first == null) 
		{
			first = new QueueElement(msg);
		}
		QueueElement e = first;
		while(e.next != null) 
		{
			e = e.next;
		}
		e.next = new QueueElement(msg);
		
	}
	public void addToHead(TCPMessage msg)
	{
		QueueElement e = new QueueElement(msg);
		e.next = first;
		first = e;
	}
	
	public TCPMessage getHead() 
	{
		TCPMessage msg = first.msg;
		first = first.next;
		return msg;
	}
	
	public boolean isEmpty() 
	{
		return first == null;
	}
	
}
