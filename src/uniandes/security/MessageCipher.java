package uniandes.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MessageCipher 
{
	public static final String DEFAULT_KEY = "000000000000000000000000";
	
	public static final String DEFAULT_ALGORITHM = "DES";
	public static final String DEFAULT_ENCODING = "Cp1252";
	
	private Cipher cipher;
	private Cipher decoder;
	private String strKey;
	private SecretKey key;
	
	public MessageCipher()
	{
		try 
		{
			cipher = Cipher.getInstance(MessageCipher.DEFAULT_ALGORITHM);
			decoder = Cipher.getInstance(MessageCipher.DEFAULT_ALGORITHM);
			DESKeySpec keySpec = new DESKeySpec(DEFAULT_KEY.getBytes());
			key = SecretKeyFactory.getInstance(MessageCipher.DEFAULT_ALGORITHM).generateSecret(keySpec);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			decoder.init(Cipher.DECRYPT_MODE, key);
		}		  
		catch (InvalidKeyException e) 
		{
			e.printStackTrace();
		}
		catch (NoSuchPaddingException e) 
		{
			e.printStackTrace();
		}			
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		} 
		catch (InvalidKeySpecException e) 
		{
			e.printStackTrace();
		}
	}

	public void setKey(String stringKey)
	{
		strKey = stringKey;
		DESKeySpec keySpec;
		try 
		{
			keySpec = new DESKeySpec(strKey.getBytes());
			key = SecretKeyFactory.getInstance(MessageCipher.DEFAULT_ALGORITHM).generateSecret(keySpec);
		} 
		catch (InvalidKeyException e) 
		{
			e.printStackTrace();
		} 
		catch (InvalidKeySpecException e) 
		{
			e.printStackTrace();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();
		}
			
	}
	
	public String encrypt(String message) 
	{
		try
		{
			byte[] encodedMessage = message.getBytes(MessageCipher.DEFAULT_ENCODING);
			byte[] encryptedBytes = cipher.doFinal(encodedMessage);
			
			return new sun.misc.BASE64Encoder().encode(encryptedBytes);			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String decrypt(String encryptedMessage) 
	{
		try
		{
			byte[] decodedBase64Bytes = new sun.misc.BASE64Decoder().decodeBuffer(encryptedMessage);
			byte[] decryptedBytes = decoder.doFinal(decodedBase64Bytes);
			String decryptedMessage = new String(decryptedBytes);
			
			return decryptedMessage;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
