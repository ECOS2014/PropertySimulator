package uniandes.propertysimulator.processor;

import uniandes.propertysimulator.world.QueueNotifications;

public class ManagerNotificationThreads implements Runnable {

	private long countThreads;
	@Override
	public void run() {
		countThreads =0;
		while(true){
			if(QueueNotifications.getInstance().hasElements() && AdminThreads.getInstance().hasLeader()){
				countThreads++;
				AdminThreads.getInstance().runLeader(QueueNotifications.getInstance().getFirstEvent(), countThreads);
				
			}
				
		}

	}



}
