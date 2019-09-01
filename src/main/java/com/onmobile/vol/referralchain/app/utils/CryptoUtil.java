package com.onmobile.vol.referralchain.app.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CryptoUtil {

	private static Logger logger = LogManager.getLogger(CryptoUtil.class);

	private CryptoConfig msisdnCryptoConfig;

	public CryptoUtil(CryptoConfig msisdnCryptoConfig) {
		this.msisdnCryptoConfig = msisdnCryptoConfig;
		this.PASSPHRASE_FOR_KEY = msisdnCryptoConfig.getKeyPassphrase();
		this.ALGO_TRANSFORMATION_STRING = msisdnCryptoConfig.getCryptoAlgorithm();
		this.DEFAULT_KEY_OBTENTION_ITERATIONS = msisdnCryptoConfig.getKeyObtentionIterations();
		init();
	}

	private final String PASSPHRASE_FOR_KEY;
	private final String ALGO_TRANSFORMATION_STRING;
	private final Charset CHARACTER_ENCODING = StandardCharsets.UTF_8;

	private final int DEFAULT_KEY_OBTENTION_ITERATIONS;

	private List<Cipher> encryptCiphers;
	private List<Cipher> decryptCiphers;

	private AtomicLong encryptCounter = new AtomicLong(0L);
	private AtomicLong decryptCounter = new AtomicLong(0L);

	private final Charset ENCRYPTED_MESSAGE_CHARSET = StandardCharsets.US_ASCII;

	public void init() {

		encryptCiphers = new ArrayList<>(msisdnCryptoConfig.getCiphersPoolSize());
		decryptCiphers = new ArrayList<>(msisdnCryptoConfig.getCiphersPoolSize());

		for (int i = 0; i < msisdnCryptoConfig.getCiphersPoolSize(); i++) {
			encryptCiphers.add(initializeCipher(Cipher.ENCRYPT_MODE));
			decryptCiphers.add(initializeCipher(Cipher.DECRYPT_MODE));
		}

	}

	public String encryptText(String clearTextMessage, Cipher c) {
		byte[] plainTextInByteArr = null;

		try {
			synchronized (c) {
				plainTextInByteArr = c.doFinal(clearTextMessage.getBytes(CHARACTER_ENCODING));
			}
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.error("IllegalBlockSizeException | BadPaddingException occured while encrypting text", e);
		}
		plainTextInByteArr = Base64.getEncoder().encode(plainTextInByteArr);

		return new String(plainTextInByteArr, ENCRYPTED_MESSAGE_CHARSET);
	}

	public String decryptCipherText(String cipherText, Cipher c) {
		byte[] encryptedMessageBytes = null;

		encryptedMessageBytes = cipherText.getBytes(ENCRYPTED_MESSAGE_CHARSET);

		encryptedMessageBytes = Base64.getDecoder().decode(encryptedMessageBytes);

		byte[] message = null;
		synchronized (c) {
			try {
				message = c.doFinal(encryptedMessageBytes);

			} catch (IllegalBlockSizeException | BadPaddingException e) {
				logger.error(
						"IllegalBlockSizeException | BadPaddingException occured while encrypting text. Message: {}",
						e.getMessage());
			}
		}
		return new String(message, CHARACTER_ENCODING);
	}

	public Cipher initializeCipher(int cipherMode) {
		Cipher c = null;

		try {
			c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);

			byte[] salt = generateSalt(c.getBlockSize());

			String nfcNormalizedPassword = Normalizer.normalize(PASSPHRASE_FOR_KEY, Normalizer.Form.NFC);

			PBEKeySpec pbeKeySpec = new PBEKeySpec(nfcNormalizedPassword.toCharArray());

			SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGO_TRANSFORMATION_STRING, c.getProvider());

			SecretKey key = factory.generateSecret(pbeKeySpec);

			PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, DEFAULT_KEY_OBTENTION_ITERATIONS);

			c.init(cipherMode, key, parameterSpec);

		} catch (NoSuchAlgorithmException e) {
			logger.error(
					"NoSuchAlgorithmException occured while getting cipher instance for the specified algo: {}, Message: {}",
					ALGO_TRANSFORMATION_STRING, e.getMessage());

		} catch (NoSuchPaddingException e) {
			logger.error(
					"NoSuchPaddingException occured while getting cipher instance for the specified algo: {}, Message: {}",
					ALGO_TRANSFORMATION_STRING, e.getMessage());

		} catch (InvalidKeyException | InvalidAlgorithmParameterException | InvalidKeySpecException e) {
			logger.error(
					"InvalidKeyException | InvalidAlgorithmParameterException | InvalidKeySpecException occured while getting PBE Spec. Message: {}",
					e.getMessage());
		}

		return c;
	}

	private byte[] generateSalt(final int lengthBytes) {
		final byte[] result = new byte[lengthBytes];
		Arrays.fill(result, (byte) 0);
		return result;
	}

	public String encrypt(String plainText) {
		return encryptText(plainText,
				encryptCiphers.get(Math.toIntExact((encryptCounter.getAndIncrement() % encryptCiphers.size()))));
	}

	public String decrypt(String cipherText) {
		return decryptCipherText(cipherText,
				decryptCiphers.get(Math.toIntExact((decryptCounter.getAndIncrement() % decryptCiphers.size()))));
	}

}