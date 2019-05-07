package com.wonder.javacar.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class FingerManagerService implements FingerManagerI{

	/* Applet Constants */
	public static final byte CLA_MONAPPLET = (byte) 0xB0;
	public static final byte INS_SET_FPT = 0x04;
	public static final byte INS_GET_FPT = 0x05;
	
	public static byte[] finger = new byte[] { 68, 42, 16, 25, -111, 74, 83, 66, 95, 13, -27, -122, 72, 22, 103, -125, 50, 23, -13,
			-119, 80, 27, -24, -127, 114, 37, -29, -121, 68, 42, 110, -125, 32, 43, 0, -119, 82, 45, -21, -124, 55,
			51, -7, -116, 29, 64, -121, -124, 118, 70, 97, -123, 20, 72, 16, -116, 43, 74, -3, -115, 53, 75, 107, 8,
			28, 79, -119, -113, 91, 92, 90, 6, 110, 92, -37, -124, 68, 93, -33, -122, 87, 94, -44, -125, 98, 111,
			84, -125, 71, 112, 82, -119, 115, 114, 84, 6, 24, 118, -97, -111, 37, 121, 79, 91, 34, 123, 54, 75, 69,
			124, 69, -118, 36, 125, 68, 43, 9, -121, -71, 5, 13, -120, 62, -120, 20, -120, 72, -114, 103, -116, -63,
			-123, 31, -105, -53, 60, 96, -105, 62, 7, 102, -105, 70, 7, 117, -102, -58, 6, 21, -100, -51, -104, 54,
			-95, 42, -120, 65, -93, 49, -120, 66, -92, 45, -120, 31, -79, 1, -112, 22, -77, -124, 14, 14, -71, -9,
			11, -1, -1, -1, 18, 34, 51, -1, -1, -1, -1, -15, 34, 34, 51, 63, -1, -1, -1, 17, 34, 35, 51, 51, -1, -1,
			-1, 17, 34, 35, 51, 51, 79, -1, -16, 17, 34, 35, 51, 51, 69, -1, -16, 17, 34, 34, 51, 51, 68, -1, -16,
			1, 18, 35, 51, 51, 68, -1, -2, 1, 18, 51, 51, 68, 68, -1, -34, 1, 34, 51, 51, 68, 69, -1, -34, 1, 35,
			51, 68, 68, 69, -1, -50, 1, 35, 52, 68, 68, 85, -5, -50, 18, 51, 68, 68, 68, 85, -5, -66, 18, 52, 68,
			85, 68, 85, -6, -67, 19, 68, 68, 85, 85, 85, -6, -83, 19, 69, 85, 85, 85, 86, -7, -101, 101, 86, 102,
			102, 102, 102, -7, -104, 119, 119, 118, 102, 102, 102, -8, -121, 120, 119, 119, 119, 118, 102, -9, 119,
			-120, -120, 119, 119, 118, 127, -1, 92, -70, -87, -104, -120, 119, 127, -1, 78, -53, -86, -103, -120,
			119, 127, -1, 62, -53, -70, -87, -104, -120, -1, -14, 46, -36, -70, -86, -103, -97, -1, -15, 30, -36,
			-69, -86, -86, -1, -1, 0, 14, -36, -53, -70, -86, -1, -1 };

	
	@Override
	public void fingerprintInit(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			System.out.print("\nFinger Print Template Value Loading...");
			_paramAPDU[3] = (byte) _data.length;
			System.arraycopy(finger, 0, _data, 0, finger.length);
			r = channel.transmit(new CommandAPDU(CLA_MONAPPLET, INS_SET_FPT, _paramAPDU[2], _paramAPDU[3], _data, _paramAPDU[5]));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// response formatter
		ApduConvertUtility.apduReturnValue(ApduConvertUtility.sw1sw2ToHexa(r)[0], ApduConvertUtility.sw1sw2ToHexa(r)[1], r.getData());
	}
	
	@Override
	public void fingerprintRetrieve(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			System.out.print("\nRetrive Finger Print Template");
			_paramAPDU[5] = (byte) 00;
			r = channel.transmit(new CommandAPDU(CLA_MONAPPLET, INS_GET_FPT, _paramAPDU[2], _paramAPDU[3], _data, _paramAPDU[5]));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// response formatter
		ApduConvertUtility.apduReturnValue(ApduConvertUtility.sw1sw2ToHexa(r)[0], ApduConvertUtility.sw1sw2ToHexa(r)[1], r.getData());
	}
}
