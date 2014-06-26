package uniandes.propertysimulator.entities;

import java.util.Date;

public class Notification {

	String line;
	Date startDateHome;
	int port;
	String ip;
	int propertyId;
	
	
	public Notification(int propertyId, String line, Date start, int port, String ip) {
		super();
		this.line = line;
		this.startDateHome = start;
		this.port = port;
		this.ip = ip;
		this.propertyId = propertyId;
	}
	
	
	
	public int getPropertyId() {
		return propertyId;
	}


	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}


	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public Date getStartDateHome() {
		return startDateHome;
	}
	public void setStartDateHome(Date currentDate) {
		this.startDateHome = currentDate;
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
