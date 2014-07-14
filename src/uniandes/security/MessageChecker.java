package uniandes.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageChecker 
{
	private static String DEFAULT_KEY = "000000000000000000000000";
	private static String DEFAULT_ALGORITHM = "MD5";
	
	private MessageDigest messageDigest;
	
	public MessageChecker()
	{
		try 
		{
			messageDigest = MessageDigest.getInstance(DEFAULT_ALGORITHM);
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getHash(String message, String strKeyDate)
	{
		//String ultimateMessageToHash = DEFAULT_KEY + message + strKeyDate;
		//TODO Cambiar cuando se mantenga conexiones
		String ultimateMessageToHash = DEFAULT_KEY + message;
		byte[] hashedMessage = messageDigest.digest(ultimateMessageToHash.getBytes());
		String base64Hash =new sun.misc.BASE64Encoder().encode(hashedMessage);
		return base64Hash;
	}
	
	public boolean checkHash(String messageHash, String message, String strKeyDate)
	{
		String hashedMessage = getHash(message, strKeyDate);
		return hashedMessage.equals(messageHash);
	}
}
