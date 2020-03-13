package Sim;
import java.util.ArrayList;
import java.lang.Math;


// This class implements a node (host) it has an address, a peer that it communicates with
// and it count messages send and received.

public class Node extends SimEnt {
	//private NetworkAddr _id;
	private SimEnt _peer;
	private int _sentmsg=0;
	private int _seq = 0;
	private NetworkAddr oldAddress;
	private boolean setup = true;
	private ArrayList<TCPConnection> connections = new ArrayList<TCPConnection>();
	

	
	public Node (int network, int node)
	{
		super();
		setNetworkAddr(network, node);
		//_id = new NetworkAddr(network, node);
	}	
	
	
	// Sets the peer to communicate with. This node is single homed
	
	public void setPeer (SimEnt peer)
	{
		_peer = peer;
		
		if(_peer instanceof Link )
		{
			 ((Link) _peer).setConnector(this);
		}
	}
	/*
	
	public NetworkAddr getAddr()
	{
		return _id;
	}
	*/
//**********************************************************************************	
	// Just implemented to generate some traffic for demo.
	// In one of the labs you will create some traffic generators
	
	private int _stopSendingAfter = 0; //messages
	private int _timeBetweenSending = 10; //time between messages
	private int _toNetwork = 0;
	private int _toHost = 0;
	
	public void StartSending(int network, int node, int number, int timeInterval, int startSeq)
	{
		_stopSendingAfter = number;
		_timeBetweenSending = timeInterval;
		_toNetwork = network;
		_toHost = node;
		_seq = startSeq;
		send(this, new TimerEvent(),0);	
	}
	private int swapInterfaceAfter = 0;
	private int swapRouterAfter = 0;
	private int swapTo;
	private Router _newRouter;
	/**
	 * After non zero number of messages it will attempt swap to given interface
	 * @param numberOfMessages
	 * @param swapToInterface
	 */
	public void changeInterfaceAfter(int numberOfMessages, int swapToInterface) {
		swapInterfaceAfter= numberOfMessages;
		swapTo = swapToInterface;
	}
	
	public void changeRouterAfter(int numberOfMessages, Router newRouter) {
		_newRouter = newRouter;
		swapRouterAfter = numberOfMessages;
	}
	
	public void sendSolicitationRequest()
	{
		System.out.println(this.toString() + " sends a solicitation request");
		
		send(_peer, new Solicit(this._id, 0), 0);
	}
	
//**********************************************************************************	
	
	// This method is called upon that an event destined for this node triggers.
	public void recv(SimEnt src, Event ev)
	{
		
		if (ev instanceof TimerEvent)
		{
			for (TCPConnection con : connections) {
				if (con.getDuplicateAcks() >= 3) {
					con.setCongestionSize((int)Math.ceil(con.getCongestionSize()/2.0));
				}
			}
			if (setup && _stopSendingAfter > _sentmsg && ((_sentmsg != swapRouterAfter&&_sentmsg != swapInterfaceAfter)||_sentmsg == 0))
			{
				_sentmsg++;
				send(_peer, new Message(_id, new NetworkAddr(_toNetwork, _toHost),_seq),0);
				send(this, new TimerEvent(),_timeBetweenSending);
				System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" sent message with seq: "+_seq + " at time "+SimEngine.getTime());
				_seq++;
			}
			else if(setup && _sentmsg == swapInterfaceAfter)
			{
				_sentmsg++;
				System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() + " sends a request to change interface to interface " + swapTo);
				send(_peer, new Migrate(getAddr(), swapTo),0);
				send(this, new TimerEvent(), _timeBetweenSending);
				
			}
			else if(setup && _sentmsg == swapRouterAfter) {
				_sentmsg++;
				send(_peer,new Disconnect(getAddr()), 0);
				Link newLink = new Link();
				newLink.setConnector(this);
				_peer = newLink;
				int i = _newRouter.getEmptyInterface();
				if(i>=0) {
					_newRouter.connectInterface(i, newLink, this);	
				}
				setup = false;
				sendSolicitationRequest();
			}
		}
		else if(ev instanceof ProvideNewAddr) 
		{
			ProvideNewAddr pna = (ProvideNewAddr)ev;
			_toNetwork = pna.source().networkId();
			_toHost = pna.source().nodeId();
		}
		else if(ev instanceof Migrate) 
		{
			Migrate mig = (Migrate)ev;
			if(mig.getNewLink() != null) {
				_peer = mig.getNewLink();
				System.out.println("Node migrated to new interface");
			}
			else 
				System.out.println("Node did not migrate");
			
		}
		else if(ev instanceof UniqueAddr) {
			if(((UniqueAddr) ev).isUnique()) {
				NetworkAddr addr = ((UniqueAddr) ev).getAddr();
				oldAddress = new NetworkAddr(_id.networkId(), _id.nodeId());
				_id.updateAddr(addr.networkId(), addr.nodeId());
				send(_peer, new BindingRequest(_id, new NetworkAddr(oldAddress.networkId(), 0), _sentmsg, oldAddress),0);
				_sentmsg++;
				System.out.println("Node is ready to communicate with new address " +toString());
				setup = true;
			}
			else {
				System.out.println(toString() +" did not find unique address testing new address");
				((UniqueAddr)ev).getAddr().incrementAddr();
				send(_peer, ev, 0);
			}
		}
		else if(ev instanceof BindingAck) 
		{
			System.out.println("Binding complete");
		}
		else if(ev instanceof Advertisement)
		{
			if(!setup) {
				NetworkAddr addr = new NetworkAddr(((Advertisement)ev).source().networkId(),((Advertisement)ev).source().networkId()+1 );
				send(_peer, new UniqueAddr(addr), 0);
			}
			System.out.println(this.toString() + " Received Advertisement from Router " + ((Advertisement)ev).source());
		}
		else if (ev instanceof Message)
		{
			System.out.println("Node "+_id.networkId()+ "." + _id.nodeId() +" receives message with seq: "+((Message) ev).seq() + " at time "+SimEngine.getTime());
		}
		if(ev instanceof ForwardMessage) {
			ForwardMessage fm = (ForwardMessage) ev;
			send(_peer, new ProvideNewAddr(_id,fm.getOriginalSource(),_seq) , 0);
			System.out.println("Send new address to other host");
		}
	}
}
