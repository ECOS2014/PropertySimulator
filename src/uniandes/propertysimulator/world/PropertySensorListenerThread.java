package uniandes.propertysimulator.world;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import uniandes.security.MessageCipher;

public class PropertySensorListenerThread implements Runnable
{
	private static final int DEFAULT_BUFFER_SIZE = 512;
	
	private Socket sensorSocket;
	private InputStream sensorInputStream;
	private byte[] sensorListenerBuffer;
	private int propertyId;
	private String centralIP;
	private int centralListeningPort;
	private Socket propertyHouseSocket; 
	private OutputStream propertyOutputStream;
	
	public PropertySensorListenerThread(Socket sensorSocket, int propertyId, String centralIP, int centralListeningPort)
	{
		System.out.println("New socket");
		
		this.sensorSocket = sensorSocket;
		this.propertyId = propertyId;
		this.centralIP = centralIP;
		this.centralListeningPort = centralListeningPort;
		
		try 
		{
			propertyHouseSocket = new Socket(centralIP, centralListeningPort);
			propertyOutputStream = propertyHouseSocket.getOutputStream();
		} 
		catch (UnknownHostException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		sensorListenerBuffer = new byte[DEFAULT_BUFFER_SIZE];
		try 
		{
			sensorInputStream = this.sensorSocket.getInputStream();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() 
	{
		while(true)
		{
			try 
			{
				sensorInputStream.read(sensorListenerBuffer);
				Date currentDate = new Date();
				Random random = new Random();
								
				byte statusSensor=sensorListenerBuffer[0];
				
				int sensorType = statusSensor%2; //B
				int status = (statusSensor/2)%2; //A
				int systemActive = random.nextInt(2); //C
				int typeNotification=0;
				if((sensorType==1 || systemActive==1) && (status==1)){
					typeNotification=1;
				}
				
				//casa;sensor;status;typesensor;systemActive;typeNotification;milisengundo invertidos en la casa
				String parsedLine = new String((propertyId+";"+sensorListenerBuffer[1]+";"+status+";"+sensorType+";"+systemActive+";"+typeNotification).trim());
				SendServerNotification(parsedLine,currentDate);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private void SendServerNotification(String line, Date startDate) 
 	{
 		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
 		try 
 		{
 			//Socket socket = new Socket(centralIP, centralListeningPort); 
 			//OutputStream outputStream = socket.getOutputStream();
 			
 			//se concantena los milisegundos invertidos en la casa
 			Date dateEnd = new Date();
 			long milliseconds = dateEnd.getTime() - startDate.getTime();
 			
 			line+= ";"+milliseconds+";"+df.format(startDate)+";"+df.format(dateEnd);
 			
 			MessageCipher ms = new MessageCipher();
 			line = ms.encrypt(line);
 			propertyOutputStream.write(line.getBytes()); 
 			
 			//propertyOutputStream.close();
 			//propertyHouseSocket.close();
 			
 			System.out.println("Se envio una notificacion: "+line);
 		} 
 		catch (Exception e) 
 		{
 			System.out.println("Property " + propertyId + " Couldn\'t find central server at " + centralIP + ":" + centralListeningPort);
 		}
 	}
}