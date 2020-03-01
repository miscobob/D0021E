package Sim;

public class UniqueAddr implements Event {

	private boolean unique = false;
	private NetworkAddr newAddr;
	public UniqueAddr(NetworkAddr addr) {
		newAddr = addr;
	}
	
	public NetworkAddr getAddr() {
		return newAddr;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	public void entering(SimEnt locale) {
		// TODO Auto-generated method stub
		
	}
	
	
}
