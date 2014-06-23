package uniandes.propertysimulator.world;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Random;

public class PropertySensorListenerServer implements IStoppable
{
	private static final String CONFIG_FILE_PATH = "./data/config.properties";
	private static final String KEY_LISTENING_PORT = "listeningPort";
	private static final String KEY_CENTRAL_IP = "centralIP";
	private static final String KEY_CENTRAL_LISTENING_PORT = "centralPort";
	
	private String propertyId;
	private String centralIP;
	private int centralListeningPort;
	private ServerSocket server = null;
	private byte[] defaultBufferReader;
	
	public PropertySensorListenerServer() 
	{
		propertyId = getRandomId();
		defaultBufferReader = new byte[512];
		Thread shutdownMonitor = new Thread(new ShutDownMonitor(this));
		shutdownMonitor.setDaemon(true);
		shutdownMonitor.start();
		
		try 
		{
			Properties configProperties = loadProperties();
			initServerSocket(configProperties);
			initCentralInfo(configProperties);
			startListening();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private String getRandomId() 
	{
		int idLength = 10;
		
		String stringId = new String();
		Random random = new Random();
		for (int i = 0; i < idLength; i++)
		{
			stringId += ("" + random.nextInt(10));
		}		
		
		return stringId;
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
		System.out.println("Property id: " + propertyId);
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
		try
		{
			while (true)
			{
				Socket socketObject = server.accept();
				InputStream reader = socketObject.getInputStream();
				reader.read(defaultBufferReader);
				//TODO:Cambiar, enviar los bytes
				
				
				
				String line = new String(defaultBufferReader);
				System.out.println("Reading: " + line.trim());
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
			String message = "{propertyId:" + propertyId + ",sensorMessage:" + line.trim() +"}";
			System.out.println(message);
			Socket socket = new Socket(centralIP, centralListeningPort);
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(message.getBytes());
			outputStream.close();
			socket.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Property " + propertyId + " Couldn\'t find central server at " + centralIP + ":" + centralIP);
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
