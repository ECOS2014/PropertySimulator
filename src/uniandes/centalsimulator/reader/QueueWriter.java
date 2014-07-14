package uniandes.centalsimulator.reader;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class  QueueWriter  {
	private ConcurrentLinkedQueue<String> lines;
	private ConcurrentLinkedQueue<String> milliseconds;
	private String ip = "None";
	public static int port = -1;
	
	public void setIp(String ip) {
		this.ip = ip;
	}

	private static QueueWriter queueAlarms;
	
	public static QueueWriter getInstance(){
		if(queueAlarms ==null){
			queueAlarms = new QueueWriter();
		}
		return queueAlarms;
	}
	
	private QueueWriter(){
		lines = new ConcurrentLinkedQueue<String>();
		milliseconds = new ConcurrentLinkedQueue<String>();
		try 
		{
			ip = InetAddress.getLocalHost().getHostAddress();
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
	}

	public void markStartRecovery(String threadId)
	{
		long millisecs = (new Date()).getTime();
		milliseconds.add("RBegin:"+threadId+":"+millisecs);
	}
	
	public void markEndRecovery(String threadId)
	{
		long millisecs = (new Date()).getTime();
		milliseconds.add("REnd:"+threadId+":"+millisecs);
	}
	
	public void putLine(String line, String millisecs) {
		lines.add("Server at " + ip + " solve = " + line);
		milliseconds.add(ip + ":" + port + ";" + millisecs);
	}

	public String getFirstLine() {
		return lines.poll();
	}
	
	public String getFirstMilliseconds() {
		return milliseconds.poll();
	}
}

	