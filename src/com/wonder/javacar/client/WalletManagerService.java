package com.wonder.javacar.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

public class WalletManagerService implements WalletManagerI {

	/* Applet Constants */
	public static final byte CLA_MONAPPLET = (byte) 0xB0;
	public static final byte INS_INCREMENTER_COMPTEUR = 0x00;
	public static final byte INS_DECREMENTER_COMPTEUR = 0x01;
	public static final byte INS_INTERROGER_COMPTEUR = 0x02;
	public static final byte INS_INITIALISER_COMPTEUR = 0x03;
	
	private static InputStreamReader isr = null;
	private static BufferedReader br = null;
	
	public WalletManagerService() {
		if(isr == null) {
			isr = new InputStreamReader(System.in);
			br = new BufferedReader(isr);
		}
	}
	
	@Override
	public void walletInit(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		byte inc = 0x00;
		try {
			do {
				System.out.print("\nType Increment Value: ");
				inc = Byte.valueOf(br.readLine());
			} while (inc > 0);

			_paramAPDU[3] = (byte) _data.length;
			_data[0] = inc;

			r = channel.transmit(new CommandAPDU(CLA_MONAPPLET, INS_INITIALISER_COMPTEUR, _paramAPDU[2], _paramAPDU[3],
					_data, _paramAPDU[5]));
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
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
	public void walletIncrement(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			r = channel.transmit(new CommandAPDU(CLA_MONAPPLET, INS_INCREMENTER_COMPTEUR, _paramAPDU[2], _paramAPDU[3],
					_data, _paramAPDU[5]));
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// response formatter
		ApduConvertUtility.apduReturnValue(ApduConvertUtility.sw1sw2ToHexa(r)[0], ApduConvertUtility.sw1sw2ToHexa(r)[1], r.getData());
		
	}

	@Override
	public void walletDecrement(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			r = channel.transmit(new CommandAPDU(CLA_MONAPPLET, INS_DECREMENTER_COMPTEUR, _paramAPDU[2], _paramAPDU[3],
					_data, _paramAPDU[5]));
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// response formatter
		ApduConvertUtility.apduReturnValue(ApduConvertUtility.sw1sw2ToHexa(r)[0], ApduConvertUtility.sw1sw2ToHexa(r)[1], r.getData());
		
	}

	@Override
	public void walletCheck(byte[] _paramAPDU, byte[] _data, CardChannel channel) {
		ResponseAPDU r = null;
		try {
			r = channel.transmit(new CommandAPDU(CLA_MONAPPLET, INS_INTERROGER_COMPTEUR, _paramAPDU[2], _paramAPDU[3],
					_data, _paramAPDU[5]));
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// response formatter
		ApduConvertUtility.apduReturnValue(ApduConvertUtility.sw1sw2ToHexa(r)[0], ApduConvertUtility.sw1sw2ToHexa(r)[1], r.getData());
	}


}
