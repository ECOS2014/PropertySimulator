package uniandes.propertysimulator.world;

public class TimeOutShutDown implements Runnable
{
	private long timeOut;
	private IStoppable stoppable;
	
	public TimeOutShutDown(IStoppable stoppable, long timeOut)
	{
		this.timeOut = timeOut;
		this.stoppable = stoppable;
	}
	
	@Override
	public void run() 
	{
		try
		{
			Thread.sleep(timeOut);			
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		if (stoppable != null)
		{
			stoppable.shutdown();
		}
	}	
}
