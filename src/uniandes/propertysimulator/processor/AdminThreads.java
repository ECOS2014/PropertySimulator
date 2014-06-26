package uniandes.propertysimulator.processor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import uniandes.propertysimulator.entities.Notification;

public class AdminThreads {

	private ConcurrentLinkedQueue<ThreadNotificationResolver> threads;
	private static AdminThreads singleton;
	private static int MAX_THREADS;
	private static int MIN_TO_CREATE_FOLLOWERS;
	private static int NUMBER_FOLLOWERS_TO_CREATE;
	private static int TOTAL_THREADS;

	private static final String CONFIG_FILE_PATH = "./data/config.properties";
	private static final String KEY_MAX_THREADS = "maxThreads";
	private static final String KEY_MIN_TO_CREATE_FOLLOWERS = "minToCreateFollowers";
	private static final String KEY_NUMBER_FOLLOWERS_TO_CREATE = "numberFollowersToCreate";
	private static final String KEY_TOTAL_THREADS = "totalThreads";
	
	public static AdminThreads getInstance(){
		if(singleton == null)
			singleton = new AdminThreads();
		return singleton;
	}

	private AdminThreads(){
		threads = new ConcurrentLinkedQueue<ThreadNotificationResolver>();
		initProperties();
		this.createFollowers();

	}

	private void createFollowers() {
		ThreadNotificationResolver alarmResolver;
		int totalToCreate = TOTAL_THREADS+NUMBER_FOLLOWERS_TO_CREATE >= MAX_THREADS? MAX_THREADS - TOTAL_THREADS:NUMBER_FOLLOWERS_TO_CREATE;
		
		if(TOTAL_THREADS< MAX_THREADS && totalToCreate > 0){
			for(int i=0; i<totalToCreate; i++){
				TOTAL_THREADS++;
				alarmResolver = new ThreadNotificationResolver(TOTAL_THREADS);
				threads.add(alarmResolver);
			}
		}

	}

	public void putFollower(ThreadNotificationResolver alarmResolver) {
		threads.add(alarmResolver);
	}

	public void runLeader(Notification notifications, long threadNumber) {
		ThreadNotificationResolver notificationResolver;
		Thread resolver; 
		notificationResolver =threads.poll();
		notificationResolver.SetNumber(threadNumber);
		this.validateNumberThreads();

		if(notificationResolver !=null)
		{
			notificationResolver.setNotification(notifications);
			resolver = new Thread(notificationResolver); 
			resolver.start();
		}


	}

	private void validateNumberThreads(){
		//Evalua si debe crear mas threads
		if(threads.size()<MIN_TO_CREATE_FOLLOWERS){
			NUMBER_FOLLOWERS_TO_CREATE*=2;
			MIN_TO_CREATE_FOLLOWERS = NUMBER_FOLLOWERS_TO_CREATE - (NUMBER_FOLLOWERS_TO_CREATE/2);
			createFollowers();
		}
	}

	public boolean hasLeader(){
		return !threads.isEmpty();
	}
	
	/**
	 * CARGA DE PARAMETROS
	 */
	private void initProperties() 
	{
		Properties properties = loadProperties();
		String maxThreads = properties.getProperty(KEY_MAX_THREADS);
		String minToCreateFollowers = properties.getProperty(KEY_MIN_TO_CREATE_FOLLOWERS);
		String numberFollowersToCreate = properties.getProperty(KEY_NUMBER_FOLLOWERS_TO_CREATE);
		String totalThreads = properties.getProperty(KEY_TOTAL_THREADS);
		MAX_THREADS =  Integer.parseInt(maxThreads);
		MIN_TO_CREATE_FOLLOWERS =  Integer.parseInt(minToCreateFollowers);
		NUMBER_FOLLOWERS_TO_CREATE =  Integer.parseInt(numberFollowersToCreate);
		TOTAL_THREADS =  Integer.parseInt(totalThreads);
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

}
