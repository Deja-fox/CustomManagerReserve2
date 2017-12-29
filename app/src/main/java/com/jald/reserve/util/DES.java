package com.jald.reserve.util;

import android.annotation.SuppressLint;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {

	public static final String TAG = "DES";

	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	@SuppressLint("TrulyRandom")
	public static String encryptDES(String encryptString, String encryptKey) throws GeneralSecurityException {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = null;
		try {
			encryptedData = encryptString.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		encryptedData = cipher.doFinal(encryptedData);
		return Base64.encode(encryptedData);
	}

	public static String decryptDES(String decryptString, String decryptKey) throws GeneralSecurityException {
		byte[] cipherData = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		byte[] keyBytes = null;
		try {
			keyBytes = decryptKey.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(cipherData);
		return new String(decryptedData);
	}
}
