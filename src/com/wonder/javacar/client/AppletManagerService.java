package com.wonder.javacar.client;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class AppletManagerService implements AppletManagerI {

	public static final String[][] HEX = new String[][] { { "A", "10" }, { "B", "11" }, { "C", "12" }, { "D", "13" },
		{ "E", "14" }, { "F", "15" } };
	
	@Override
	public void downloadApplet(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			r = channel.transmit(
					new CommandAPDU(_paramAPDU[0], _paramAPDU[1], _paramAPDU[2], _paramAPDU[3], _data, _paramAPDU[5]));
		} catch (CardException e) {
			e.printStackTrace();
		}
		System.out.println("\nDowload Response: " + sw1sw2ToHexa(r)[0] + " " + sw1sw2ToHexa(r)[1]);
		
	}

	@Override
	public void installApplet(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			r = channel.transmit(
					new CommandAPDU(_paramAPDU[0], _paramAPDU[1], _paramAPDU[2], _paramAPDU[3], _data, _paramAPDU[5]));
		} catch (CardException e) {
			e.printStackTrace();
		}
		String[] res = sw1sw2ToHexa(r);
		System.out.println("\nInstallation Response: " + res[0] + " " + res[1]);
		
	}

	@Override
	public String[] selectApplet(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		String[] res = new String[2];
		try {
			r = channel.transmit(
					new CommandAPDU(_paramAPDU[0], _paramAPDU[1], _paramAPDU[2], _paramAPDU[3], _data, _paramAPDU[5]));
		} catch (CardException e) {
			e.printStackTrace();
		}
		res = sw1sw2ToHexa(r);
		System.out.println("\nSelection Response: " + res[0] + " " + res[1]);
		return res;
	}
	
	private static String[] sw1sw2ToHexa(ResponseAPDU rapdu) {
		String[] res = new String[2];
		res[0] = fromDecToHex(rapdu.getSW1());
		res[1] = fromDecToHex(rapdu.getSW2());
		return res;
	}

	private static String fromDecToHex(int in) {
		String res = "", rDigit = "";
		// Get unsigned value
		in = in >= 0 ? in : 256 + in;
		int q = in, r = 0, base = 16;
		do {
			q = in / base;
			r = in % base;
			// cast r value
			rDigit = String.valueOf(r);
			if (r > 9 && r < base) {
				for (String[] arr : HEX) {
					if (arr[1].equals(String.valueOf(rDigit))) {
						rDigit = arr[0];
						res = rDigit + res;
						break;
					}
				}
			} else if (r < 10) {
				res = rDigit + res;
			}
			in = q;
		} while (q != 0);
		return writeOntwoDigit(res);
	}

	private static String writeOntwoDigit(String in) {
		String out = "";
		if (in.length() % 2 == 1) {
			in = "0" + in;
		}
		for (int i = 0; i < in.length(); i++) {
			out += in.charAt(i);
			if (i % 2 != 0) {
				out += ":";
			}
		}
		in = out.charAt(out.length() - 1) == ':' ? out.substring(0, out.length() - 1) : out;
		return in;
	}
}
