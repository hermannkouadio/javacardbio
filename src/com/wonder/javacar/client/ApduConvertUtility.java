package com.wonder.javacar.client;

import javax.smartcardio.ResponseAPDU;

public class ApduConvertUtility {

	public static String fromDecToHex(int in) {
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
				for (String[] arr : AppletManagerService.HEX) {
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

	public static void apduReturnValue(String sw1, String sw2, byte[] data) {
		System.out.println("\nCheck Response: " + sw1 + " " + sw2);
		System.out.print("Return Value: ");
		for (byte b : data)
			System.out.print(fromDecToHex(b) + " ");
	}
	
	public static String[] sw1sw2ToHexa(ResponseAPDU rapdu) {
		String[] res = new String[2];
		res[0] = fromDecToHex(rapdu.getSW1());
		res[1] = fromDecToHex(rapdu.getSW2());
		return res;
	}
}
