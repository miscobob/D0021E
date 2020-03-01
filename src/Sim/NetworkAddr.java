package Sim;

// This class represent the network address, it consist of a network identity
// "_networkId" represented as an integer (if you want to link this to IP number it can be
// compared to the network part of the IP address like 132.17.9.0). Then _nodeId represent
// the host part.

public class NetworkAddr {
	private int _networkId; // prefix 
	private int _nodeId; // suffix
	
	NetworkAddr(int network, int node)
	{
		_networkId=network;
		_nodeId=node;
	}
	
	public int networkId()
	{
		return _networkId;
	}
	
	public int nodeId()
	{
		return _nodeId;
	}
	
	public void updateAddr(int networkId, int nodeId) {
		_networkId = networkId;
		_nodeId = nodeId;
	}
	public void incrementAddr() {
		_nodeId++;
	}
	
	public boolean sameNetwork(NetworkAddr addr) {
		return addr._networkId == this._networkId;
	}
	
	public boolean SameAddress(NetworkAddr addr) {
		return addr.networkId() == this._networkId && addr._nodeId == this.nodeId();
	}
	
	@Override
	public String toString()
	{
		return this._networkId + "." + this.nodeId();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NetworkAddr) {
			return SameAddress((NetworkAddr)obj);
		}
		return false;
	}
	@Override
	public int hashCode() {
		return _networkId+_nodeId;
	}
}

