package uniandes.propertysimulator.processor;

import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import uniandes.propertysimulator.entities.Notification;

public class ThreadNotificationResolver implements Runnable{

	int id;
	long totalMilliseconds;
	long count;
	Notification notification;
	public ThreadNotificationResolver(int id) {
		this.id = id;
	}
	
	public void SetNumber(long count){
		this.count = count;
	}
	@Override
	public void run() {
		
		sendServerNotification();
		AdminThreads.getInstance().putFollower(this); 
	}


	public void setNotification(Notification notification){
		this.notification = notification; 		
	}
	
	private void sendServerNotification() 
	{
		Date dateEnd;
		long milliseconds;
		Date startDate;
		String line;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		try 
		{
			Socket socket = new Socket(this.notification.getIp(), this.notification.getPort()); 
			OutputStream outputStream = socket.getOutputStream();
			//se concantena los milisegundos invertidos en la casa
			dateEnd = new Date();
			startDate = this.notification.getStartDateHome();
			milliseconds = dateEnd.getTime() - startDate.getTime();
			
			line = this.notification.getLine() +";"+milliseconds+";"+df.format(startDate)+";"+df.format(dateEnd);	
			outputStream.write(line.getBytes()); 
			
			outputStream.close();
			socket.close();
			
		} 
		catch (Exception e) 
		{
			System.out.println("Property " + this.notification.getPropertyId() + " Couldn\'t find central server at " + this.notification.getIp() + ":" + this.notification.getPort());
		}
	}

}
