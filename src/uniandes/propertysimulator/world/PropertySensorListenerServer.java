package uniandes.propertysimulator.world;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

public class PropertySensorListenerServer implements IStoppable
{
	private static final String CONFIG_FILE_PATH = "./data/config.properties";
	private static final String KEY_LISTENING_PORT = "listeningPort";
	private static final String KEY_CENTRAL_IP = "centralIP";
	private static final String KEY_CENTRAL_LISTENING_PORT = "centralPort";
	
	private int propertyId;
	private String centralIP;
	private int centralListeningPort;
	private ServerSocket server = null;
	int SensorType; //B
	int Status; //A
	int SystemActive; //C
	int TypeNotification; //f(A,B,C)
	private byte[] defaultBufferReader;

	
	public PropertySensorListenerServer() 
	{
		
		defaultBufferReader = new byte[512];
		Thread shutdownMonitor = new Thread(new ShutDownMonitor(this));
		shutdownMonitor.setDaemon(true);
		shutdownMonitor.start();
		
		try 
		{
			Properties configProperties = loadProperties();
			propertyId =  Integer.parseInt(configProperties.getProperty("houseId1"));
			initServerSocket(configProperties);
			initCentralInfo(configProperties);
			startListening();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}


	private Properties loadProperties() 
	{
		Properties properties = null;
		try
		{
			FileInputStream inputStream = new FileInputStream(CONFIG_FILE_PATH);
			properties = new Properties();
			properties.load(inputStream);
			inputStream.close();			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return properties;
	}
	
	private void initServerSocket(Properties configProperties) throws IOException 
	{
		String strPortNumber = configProperties.getProperty(KEY_LISTENING_PORT);
		int portNumber = Integer.parseInt(strPortNumber);
		server = new ServerSocket(portNumber);
		System.out.println("Server started");
		System.out.println("Hit Enter to stop the server");
	}
	
	private void initCentralInfo(Properties configProperties) 
	{
		centralIP = configProperties.getProperty(KEY_CENTRAL_IP); 
		String strCentralListeningPort = configProperties.getProperty(KEY_CENTRAL_LISTENING_PORT);
		centralListeningPort = Integer.parseInt(strCentralListeningPort);
	}

	private void startListening() throws IOException 
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		Date currentDate;
		String date;
		try
		{
			while (true)
			{
				Random random = new Random();
				Socket socketObject = server.accept();
				InputStream reader = socketObject.getInputStream();
				reader.read(defaultBufferReader);
				
				byte statusSensor=defaultBufferReader[0];
				
				System.out.println("in byte "+statusSensor+" "+defaultBufferReader[1]);
				
				SensorType = statusSensor%2; //B
				Status = (statusSensor/2)%2; //A
				SystemActive =random.nextInt(2); //C
				TypeNotification=0;
				if((SensorType==1 || SystemActive==1) && (Status==1)){
					TypeNotification=1;
				}
				

				currentDate = new Date();
				date= df.format(currentDate);
				
				System.out.println("out byte "+propertyId+";"+defaultBufferReader[1]+";"+Status+";"+SensorType+";"+SystemActive+";"+TypeNotification);
				//casa;sensor;status;typesensor;systemActive;typeNotification
				String line = new String((propertyId+";"+defaultBufferReader[1]+";"+Status+";"+SensorType+";"+SystemActive+";"+TypeNotification+";"+date).trim());
				
				
				
				System.out.println("Se generó una notificación Hora: "+date+" propiedad: "+propertyId +" sensor: "+defaultBufferReader[1]);
				
				SendServerNotification(line);
			}
		}
		catch (SocketException se)
		{
			System.out.println("Server is down");
			se.printStackTrace();
		}		
	}

	private void SendServerNotification(String line) 
	{
		try 
		{
			//String message = "{propertyId:" + propertyId2+propertyId1 + ",sensorMessage:" + line.trim() +"}";
			//System.out.println(message);
			Socket socket = new Socket(centralIP, centralListeningPort); 
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(line.getBytes()); //
			
			outputStream.close();
			socket.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Property " + propertyId + " Couldn\'t find central server at " + centralIP + ":" + centralListeningPort);
		}
	}

	@Override
	public void shutdown() 
	{
		try
		{
			System.out.println("Closing");
			server.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		System.exit(0);
	}
}
