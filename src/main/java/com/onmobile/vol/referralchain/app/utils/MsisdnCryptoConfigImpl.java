package com.onmobile.vol.referralchain.app.utils;

import org.springframework.context.annotation.Configuration;

@Configuration
//@ConfigurationProperties(prefix = "uep-crusader.crypto.msisdn")
public class MsisdnCryptoConfigImpl implements CryptoConfig {

	private String cryptoAlgorithm = "PBEWithMD5AndDES";

	private String keyPassphrase = "onmobileonmobile";

	private int keyObtentionIterations = 1000;

	private int ciphersPoolSize = 100;

	@Override
	public String getCryptoAlgorithm() {
		return cryptoAlgorithm;
	}

	@Override
	public String getKeyPassphrase() {
		return keyPassphrase;
	}

	@Override
	public int getKeyObtentionIterations() {
		return keyObtentionIterations;
	}

	@Override
	public int getCiphersPoolSize() {
		return ciphersPoolSize;
	}

	public void setCryptoAlgorithm(String cryptoAlgorithm) {
		this.cryptoAlgorithm = cryptoAlgorithm;
	}

	public void setKeyPassphrase(String keyPassphrase) {
		this.keyPassphrase = keyPassphrase;
	}

	public void setKeyObtentionIterations(int keyObtentionIterations) {
		this.keyObtentionIterations = keyObtentionIterations;
	}

	public void setCiphersPoolSize(int ciphersPoolSize) {
		this.ciphersPoolSize = ciphersPoolSize;
	}
}