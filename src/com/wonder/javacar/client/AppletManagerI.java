package com.wonder.javacar.client;

import javax.smartcardio.CardChannel;

public interface AppletManagerI {

	public void downloadApplet(byte[] _paramAPDU, byte[] _data, CardChannel channel);
	public void installApplet(byte[] _paramAPDU, byte[] _data, CardChannel channel);
	public String[] selectApplet(byte[] _paramAPDU, byte[] _data, CardChannel channel);
}
