package uniandes.propertysimulator.world;
import javax.swing.JFrame;

public class PropertySimulator extends JFrame 
{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args)
	{
		PropertySimulator ps = new PropertySimulator();
		ps.setSize(800, 100);
		ps.setDefaultCloseOperation(EXIT_ON_CLOSE);
		ps.setVisible(true);		
		if (args.length == 5)
		{
			String strPropertyId = args[0];
			int propertyId = Integer.parseInt(strPropertyId);
			
			String centralIP = args[1];
			
			String strCentralPort = args[2];
			int centralPort = Integer.parseInt(strCentralPort);
			
			String strListeningPort = args[3];
			int listeningPort = Integer.parseInt(strListeningPort);
			
			String strTimeout = args[4];
			long timeout = Long.parseLong(strTimeout);
			
			ps.setTitle("{propertyId:" + propertyId + ", centralIP:" + centralIP + ", centralPort:" + centralPort + ", listeningPort:" + listeningPort + ", timeout:" + timeout + "}");
			new PropertySensorListenerServer(propertyId, centralIP, centralPort, listeningPort, timeout);
		}
		else
		{
			ps.setTitle("Property no args were found");
			new PropertySensorListenerServer();
		}
	}	
}
