package com.wonder.javacar.client;

import javax.smartcardio.CardChannel;

public interface FingerManagerI {
	public void fingerprintInit(byte[] _paramAPDU, byte[] _data, CardChannel channel) ;
	public void fingerprintRetrieve(byte[] _paramAPDU, byte[] _data, CardChannel channel) ;
}
