package com.hover.tester.gateway;

import android.annotation.TargetApi;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

public class KeyStoreHelper {

	public static String encrypt(int serviceId, String value, Context c) {
		try {
			KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
			keystore.load(null);
			KeyStore.PrivateKeyEntry privateKeyEntry = null;
			try {
				privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(getPrefix(c) + serviceId, null);
			} catch (NullPointerException e) { } // seems to be bug in keystore that throws chain == null
			if (privateKeyEntry == null) {
				generateKey(serviceId, c);
				privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(getPrefix(c) + serviceId, null);
			}
			RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

			Cipher input = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
			input.init(Cipher.ENCRYPT_MODE, publicKey);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, input);
			cipherOutputStream.write(value.getBytes("UTF-8"));
			cipherOutputStream.close();

			String encryptedText  = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
			return encryptedText;
		} catch (Exception e) {
			Crashlytics.logException(e);
			Log.e("Key Store failure", e.toString());
		}
		return null;
	}

	public static String decrypt(String encryptedPin, String actionId, Context c) {
		try {
			KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
			keystore.load(null);
			KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(getPrefix(c) + actionId, null);

			Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			output.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
			CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(Base64.decode(encryptedPin, Base64.DEFAULT)), output);
			ArrayList<Byte> values = new ArrayList<>();
			int nextByte;
			while ((nextByte = cipherInputStream.read()) != -1)
				values.add((byte) nextByte);

			byte[] bytes = new byte[values.size()];
			for (int i = 0; i < bytes.length; i++)
				bytes[i] = values.get(i);

			return new String(bytes, 0, bytes.length, "UTF-8");
		} catch (Exception e) {
			Crashlytics.logException(e);
			Log.e("Key Store failure", e.toString());
		}
		return null;
	}

	@TargetApi(18)
	public static void createNewKey(final int alias, final Context c) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				generateKey(alias, c);
			}
		}).start();
	}

	private static void generateKey(final int alias, final Context c) {
		try {
			KeyStore keystore = KeyStore.getInstance("AndroidKeyStore");
			keystore.load(null);
			if (!keystore.containsAlias(getPrefix(c) + alias)) {
				Calendar start = Calendar.getInstance();
				Calendar end = Calendar.getInstance();
				end.add(Calendar.YEAR, 1);
				KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(c)
						.setAlias(getPrefix(c) + alias)
						.setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
						.setSerialNumber(BigInteger.ONE)
						.setStartDate(start.getTime())
						.setEndDate(end.getTime())
						.build();
				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
				generator.initialize(spec);
				generator.generateKeyPair();
			}
		} catch (Exception e) {
			Crashlytics.logException(e);
			Log.e("Key Store failure", e.toString());
		}
	}

	private static String getPrefix(Context c) { return c.getPackageName() + "_"; }
}
