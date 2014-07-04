package uniandes.propertysimulator.world;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

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
	int sensorType; //B
	int status; //A
	int systemActive; //C
	int typeNotification; //f(A,B,C)

	boolean isListening;
	
	public PropertySensorListenerServer() 
	{		
		// 5 minutos 300000
		//Thread timeOutShutdown = new Thread(new TimeOutShutDown(this, 600000));
		//timeOutShutdown.setDaemon(true);
		//timeOutShutdown.start();
		
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

	public PropertySensorListenerServer(int propertyId2, String centralIP2,	int centralPort, int listeningPort, long timeout) 
	{
		Thread shutdownMonitor = new Thread(new ShutDownMonitor(this));
		shutdownMonitor.setDaemon(true);
		shutdownMonitor.start();
		
		Thread timeOutShutdown = new Thread(new TimeOutShutDown(this, timeout));
		timeOutShutdown.setDaemon(true);
		timeOutShutdown.start();
		
		try
		{
			propertyId = propertyId2;
			initServerSocket(listeningPort);
			initCentralInfo(centralIP2,centralPort);
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
		initServerSocket(portNumber);
	}
	
	private void initServerSocket(int listeningPort) throws IOException 
	{
		server = new ServerSocket(listeningPort);
		isListening = true;
		System.out.println("Server started");
		System.out.println("Hit Enter to stop the server");
	}
	
	private void initCentralInfo(Properties configProperties) 
	{
		String strCentralListeningPort = configProperties.getProperty(KEY_CENTRAL_LISTENING_PORT);
		initCentralInfo(configProperties.getProperty(KEY_CENTRAL_IP), Integer.parseInt(strCentralListeningPort));
	}

	private void initCentralInfo(String centralIP, int centralListeningPort) 
	{
		this.centralIP = centralIP; 
		this.centralListeningPort = centralListeningPort;
	}
	
	private void startListening() throws IOException 
	{
		try
		{
			while (isListening)
			{
				Socket sensorSocket = server.accept();
				Thread propertySensorListenerThread = new Thread(new PropertySensorListenerThread(sensorSocket, propertyId, centralIP, centralListeningPort));
				propertySensorListenerThread.start();
			}
		}
		catch (SocketException se)
		{
			System.out.println("Server is down");
			//se.printStackTrace();
		}		
	}
	
	@Override
	public void shutdown() 
	{
		try
		{
			isListening = false;
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
