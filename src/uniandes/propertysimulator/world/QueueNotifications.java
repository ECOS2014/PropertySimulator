package uniandes.propertysimulator.world;


import java.util.concurrent.ConcurrentLinkedQueue;

import uniandes.propertysimulator.entities.Notification;

public class QueueNotifications  {
	private ConcurrentLinkedQueue<Notification> notifications;
	
	private static QueueNotifications queueNotifications;
	
	public static QueueNotifications getInstance(){
		if(queueNotifications ==null){
			queueNotifications = new QueueNotifications();
		}
		return queueNotifications;
	}
	
	private QueueNotifications(){
		notifications = new ConcurrentLinkedQueue<Notification>();
	}
	public boolean hasElements() {
		return !notifications.isEmpty();
	}
	
	public void putEvent(Notification notification) {
		notifications.add(notification);
	}

	public Notification getFirstEvent() {
		return notifications.poll();
	}

}

	