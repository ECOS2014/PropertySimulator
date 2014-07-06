package uniandes.propertysimulator.world;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import uniandes.propertysimulator.entities.Server;

public class PropertySensorListenerServer implements IStoppable
{
	private static final String CONFIG_FILE_PATH = "./data/config.properties";
	private static final String KEY_LISTENING_PORT = "listeningPort"; 
	private static final String KEY_CENTRAL_LISTENING = "serversCentral";
	private static final String KEY_HOUSE_ID ="houseId";
	
	private int propertyId;
	private ServerSocket server = null;
	private boolean isListening;
	
	private List<Server> servers; 
	
	public PropertySensorListenerServer() 
	{		

		Thread shutdownMonitor = new Thread(new ShutDownMonitor(this));
		shutdownMonitor.setDaemon(true);
		shutdownMonitor.start();
		
		/*
		Thread timeOutShutdown = new Thread(new TimeOutShutDown(this, timeout));
		timeOutShutdown.setDaemon(true);
		timeOutShutdown.start();
		 */
		try 
		{
			Properties configProperties = loadProperties();
			propertyId =  Integer.parseInt(configProperties.getProperty(KEY_HOUSE_ID));
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
		//Los servidores que atienden la casa vienen IP1:Puerto1|IP2:Puerto2|..|IPN:PuertoN
		String infoServersCentral = configProperties.getProperty(KEY_CENTRAL_LISTENING);
		String[] serversCentral = infoServersCentral.split(";");
		String[] infoServer;
		Server server;
		
		this.servers = new LinkedList<Server>();
		for (String serverCentral : serversCentral) {
			infoServer = serverCentral.split(":");
			server = new Server(infoServer[0],Integer.parseInt(infoServer[1]));
			this.servers.add(server);
		}
	}

	
	private void startListening() throws IOException 
	{
		try
		{
			while (isListening)
			{
				Socket sensorSocket = server.accept();
				Thread propertySensorListenerThread = new Thread(new PropertySensorListenerThread(sensorSocket, propertyId, this.servers));
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
