package uniandes.propertysimulator.world;

import java.io.IOException;

public class ShutDownMonitor implements Runnable 
{
	private IStoppable stoppable;
	
	public ShutDownMonitor(IStoppable stoppable)
	{
		this.stoppable = stoppable;
	}
	
	@Override
	public void run() 
	{
		try
		{
			while (System.in.read() != '\n'){}			
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
		if (stoppable != null)
		{
			stoppable.shutdown();
		}
	}
}
