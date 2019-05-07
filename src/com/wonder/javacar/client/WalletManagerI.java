package com.wonder.javacar.client;

import javax.smartcardio.CardChannel;

public interface WalletManagerI {

	public void walletInit(byte[] _paramAPDU, byte[] _data, CardChannel channel);
	public void walletIncrement(byte[] _paramAPDU, byte[] _data, CardChannel channel);
	public void walletDecrement(byte[] _paramAPDU, byte[] _data, CardChannel channel);
	public void walletCheck(byte[] _paramAPDU, byte[] _data, CardChannel channel);
}
