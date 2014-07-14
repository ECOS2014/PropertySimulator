package uniandes.propertysimulator.world;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import uniandes.centalsimulator.reader.QueueWriter;
import uniandes.propertysimulator.entities.Server;
import uniandes.security.MessageChecker;
import uniandes.security.MessageCipher;

public class PropertySensorListenerThread implements Runnable
{
	private static final int DEFAULT_BUFFER_SIZE = 512;

	private Socket sensorSocket;
	private InputStream sensorInputStream;
	private int propertyId;
	private List<Server> servers;
	private int principalServer=0;
	private Socket connectionToCentral;
	private String keyDate;
	private String threadId;

	private int sendCount;

	public PropertySensorListenerThread(Socket sensorSocket, int propertyId, List<Server> servers)
	{
		threadId = getRandomId();
		System.out.println("New socket");
		this.sensorSocket = sensorSocket;
		this.propertyId = propertyId;
		this.servers = servers;
		connectionToCentral = getConnection();
		sendCount=0;
	}

	private String getRandomId() 
	{
		Random r = new Random();
		String randomId = "" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10) + "" + r.nextInt(10);
		return randomId;
	}

	@Override
	public void run() 
	{
		byte[] sensorListenerBuffer;
		sensorListenerBuffer = new byte[DEFAULT_BUFFER_SIZE];
		while(true)
		{
			try 
			{
				sensorInputStream = this.sensorSocket.getInputStream();
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
				sendServerNotification(parsedLine,currentDate);
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}

	private void sendServerNotification(String line, Date startDate) 
	{
		OutputStream propertyOutputStream;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date dateEnd;
		String modifiedLine;
		MessageCipher ms = new MessageCipher();
		boolean canSendData=false;
		
		while(!canSendData)
		{
			//se concantena los milisegundos invertidos en la casa
			dateEnd = new Date();
			long milliseconds = dateEnd.getTime() - startDate.getTime();
			modifiedLine = line+";"+milliseconds+";"+df.format(startDate)+";"+df.format(dateEnd);
			modifiedLine += (";" + getMessageDigest(modifiedLine, df.format(dateEnd)));
			
			sendCount++;
			//System.out.println(">>>>>"+sendCount);
			if (sendCount%2==0)
			{
				//modifiedLine = modifyLine(modifiedLine);
			}			
			
			modifiedLine = ms.encrypt(modifiedLine) + ";EOF";
			
			if(connectionToCentral == null || (connectionToCentral != null && connectionToCentral.isClosed()))
				connectionToCentral = getConnection();
			
			try 
			{
				propertyOutputStream = connectionToCentral.getOutputStream();	
				propertyOutputStream.write(modifiedLine.getBytes()); 
				System.out.println("Se envio una notificacion: "+modifiedLine);
				canSendData = true;
			} 
			catch (IOException e) 
			{
				System.out.println("Property " + propertyId + " Couldn\'t find central server at " + this.servers.get(principalServer).getIp() + ":" + this.servers.get(principalServer).getPort());
				try 
				{
					if (connectionToCentral != null)
					{
						connectionToCentral.close();
					}					
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				connectionToCentral = null;				
				connectionToCentral = getConnection();
			}
		}
	}

	private String modifyLine(String normalMessage) 
	{
		System.out.println("Old message: " + normalMessage);
		
		String[] strArr = normalMessage.split(";");
		
		String newMessage = "4";
		
		for(int i = 1; i < strArr.length; i++)
		{
			newMessage += (";" + strArr[i]);
		}

		System.out.println("New message: " + newMessage);
		
		return newMessage;
	}

	private String getMessageDigest(String message, String strDate) 
	{
		if (keyDate == null)
		{
			keyDate = strDate;
		}
		
		MessageChecker mc = new MessageChecker();
		String hashedBytes = mc.getHash(message, strDate);
		
		return hashedBytes;
	}

	private Socket getConnection()
	{
		QueueWriter.getInstance().markStartRecovery(threadId);
		Socket propertyHouseSocket = null; 
		
		boolean canConnectCentral=false;
		int numberOfServers = this.servers.size();
		Server server;
		while(!canConnectCentral)
		{
			server = this.servers.get(principalServer);
			try 
			{
				System.out.println("Se inicia conexion con el servidor: "+server.getIp()+":"+server.getPort());
				
				propertyHouseSocket = new Socket(server.getIp(), server.getPort());
				canConnectCentral= true;
				QueueWriter.getInstance().markEndRecovery(threadId);
			} 
			catch (UnknownHostException e1)
			{
				principalServer ++;
				principalServer = principalServer%numberOfServers;
				System.out.println("fallo el servidor: "+server.getIp()+":"+server.getPort());
				e1.printStackTrace();
			} catch (IOException e1) {
				principalServer ++;
				principalServer = principalServer%numberOfServers;
				System.out.println("fallo el servidor: "+server.getIp()+":"+server.getPort());
				e1.printStackTrace();
			} 
		}
		return propertyHouseSocket;	
	}
}