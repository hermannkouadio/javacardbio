package com.wonder.javacar.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class Start {

	private static AppletManagerService appletManagerService;
	private static WalletManagerService walletManagerService;
	private static FingerManagerService fingerManagerService;

	/* Applet Constants */
	public static final byte CLA_MONAPPLET = (byte) 0xB0;
	public static final byte INS_INCREMENTER_COMPTEUR = 0x00;
	public static final byte INS_DECREMENTER_COMPTEUR = 0x01;
	public static final byte INS_INTERROGER_COMPTEUR = 0x02;
	public static final byte INS_INITIALISER_COMPTEUR = 0x03;
	public static final byte INS_SET_FPT = 0x04;
	public static final byte INS_GET_FPT = 0x05;

	public static String PROTOCOL = "T=1";

	private final static int PORT = 55721;
	private static InputStreamReader isr = null;
	private static BufferedReader br = null;

	public static void main(String[] args) {
		if (isr == null) {
			isr = new InputStreamReader(System.in);
			br = new BufferedReader(isr);
		}
		appletManagerService = new AppletManagerService();
		walletManagerService = new WalletManagerService();
		fingerManagerService = new FingerManagerService();
		// CadClientInterface cad;
		Socket sock;
		String[] res = null;
		// show the list of available terminals
		try {
			System.out.println("\n\n\t############\tWelcome To Wallet Manager\t############\n\n");
			// Get CardTerminal
			CardTerminal terminal = selectCard(PROTOCOL);

			// APDU Send Ins
			byte cla, ins, p1, p2, lc, data, le;
			cla = (byte) 0xB0;
			ins = (byte) 0x20;
			p1 = p2 = (byte) 0x00;
			byte[] app_aid = new byte[] { 0x00, 0x00, 0x01, 0x11, 0x11, 0x10 };

			if (terminal.isCardPresent()) {
				System.out.println("Card is present");
			} else {
				System.out.println("No card");
			}

			// establish a connection with the card
			Card card = terminal.connect(PROTOCOL);

			/*
			 * sock = new Socket("localhost", PORT); InputStream is = sock.getInputStream();
			 * OutputStream os = sock.getOutputStream(); cad =
			 * CadDevice.getCadClientInstance(CadDevice.PROTOCOL_T0, is, os);
			 * 
			 * cad = CadDevice.getCadClientInstance(CadDevice.PROTOCOL_PCSC, null, null);
			 */

			CardChannel channel = card.getBasicChannel();
			System.out.println("\nTerminal: " + terminal.getName() + "\nCard: " + card + "\nChannel: " + channel);

			/*
			 * String[] res = setApduBasicIns(scanner,
			 * "Set CLA, INS, P1, P2 and Le value: "); for (String s : res) {
			 * System.out.print(s + " "); }
			 */
			// APDU Select
			byte _cla, _ins, _p1, _p2, _lc, _le;
			_cla = (byte) 0x00;
			_ins = (byte) 0xA4;
			_p1 = (byte) 0x04;
			_p2 = (byte) 0x00;
			_lc = (byte) 0x08;
			_le = (byte) 0x00;

			byte[] _paramAPDU = new byte[] { _cla, _ins, _p1, _p2, _lc, _le };
			// Applet AID 00 00 FF 3E 1A 11
			byte[] _appletAID = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0xFF, (byte) 0x3E, (byte) 0x1A,
					(byte) 0x11 };

			// Select applet
			res = appletManagerService.selectApplet(_paramAPDU, _appletAID, channel);
			if (res[0].equals("90") && res[1].equals("00")) {
				// Call Menu List For User Choice
				int r = menuOptions();
				byte[] _data = new byte[] { (byte) 0x00 };

				do {
					switch (r) {
					case 0:
						walletManagerService.walletIncrement(_paramAPDU, _data, channel);
						r = menuOptions();
						break;
					case 1:
						walletManagerService.walletDecrement(_paramAPDU, _data, channel);
						r = menuOptions();
						break;
					case 2:
						walletManagerService.walletCheck(_paramAPDU, _data, channel);
						r = menuOptions();
						break;
					case 3:
						walletManagerService.walletInit(_paramAPDU, _data, channel);
						r = menuOptions();
						break;
					case 4:
						fingerManagerService.fingerprintInit(_paramAPDU, _data, channel);
						r = menuOptions();
						break;
					case 5:
						fingerManagerService.fingerprintRetrieve(_paramAPDU, _data, channel);
						r = menuOptions();
						break;
					case 9:
						card.disconnect(true);
						System.out.println("\rCard disconnected with success !");
						break;
					default:
						break;
					}
				} while (r != 9);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CardTerminal selectCard(String protocol) throws CardException, NumberFormatException, IOException {
		TerminalFactory factory = TerminalFactory.getDefault();
		List<CardTerminal> terminals = factory.terminals().list();
		int i = 0;
		for (CardTerminal cardTerminal : terminals) {
			System.out.println("No-" + i + "\t" + cardTerminal.getName());
			++i;
		}
		System.out.print("\nSelect target card: ");
		int resp = Integer.parseInt(br.readLine());
		CardTerminal terminal = terminals.get(resp);
		System.out.println("\nCard \"" + terminals.get(resp).getName() + "\" selected.");

		return terminal;
	}

	/*
	 * Utility Method
	 */

	private static int menuOptions() {
		int resp = 0;
		System.out.println("\n\tSelect One Option\n");
		System.out.println("\t0- Increment Wallet");
		System.out.println("\t1- Decrement Wallet");
		System.out.println("\t2- Check Wallet Solde");
		System.out.println("\t3- Init Wallet");
		System.out.println("\t4- Init Finger Print Template");
		System.out.println("\t5- Get  Finger Print Template");
		System.out.println("\t6- Compare Finger Print Template");
		System.out.println("\t9- Exit");

		try {
			do {
				System.out.print("-> ");
				resp = Integer.parseInt(br.readLine());
			} while (resp < 0 || resp > 9);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resp;
	}

	private static String[] setApduBasicIns(Scanner sc, String label) {

		String[] res = new String[5];

		System.out.print("\n" + label);
		String[] arrItems = sc.nextLine().split(" ");
		sc.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

		try {
			for (int i = 0; i < arrItems.length; i++) {
				res[i] = arrItems[i];
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
