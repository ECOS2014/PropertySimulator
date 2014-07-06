package uniandes.propertysimulator.entities;

public class Server {

	private int port; 
	private String ip;
	
	
	public Server(String ip,int port) {
		this.port = port;
		this.ip = ip;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
}
