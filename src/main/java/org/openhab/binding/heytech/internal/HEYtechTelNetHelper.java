package org.openhab.binding.heytech.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;

public class HEYtechTelNetHelper {

	private String ip = null;
	private final static int port = 1002;
	private final static char newLineChar = 13;// NewLine

	private TelnetClient telnet = null;
	private InputStream in;
	private PrintStream out;
	private Thread inputStreamReaderThread;

	public HEYtechTelNetHelper(String ip) {
		super();
		this.ip = ip;
	}

	private String readUntil(String pattern) {
		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();
			while (true) {
				System.out.print(ch);
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void sendCommand(String command) {
		out.println(command);
		out.flush();
		System.out.println(command);
	}

	private void telnetConnect() throws SocketException, IOException {
		this.telnet = new TelnetClient();
		telnet.connect(this.ip, HEYtechTelNetHelper.port);
		out = new PrintStream(telnet.getOutputStream());
		in = telnet.getInputStream();
	}

	public void getShutterStatus() {
		try {
			telnetConnect();

			sendCommand("sop" + newLineChar + newLineChar);
			String sop = readUntil("ende_sop");
			sop.replace("start_sop", "");
			sop.replace("ende_sop", "");
			String[] shutterStatus = StringUtils.split(",");

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				telnet.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void doShutterAction(int kanal, String command) {
		try {
			telnetConnect();

			sendCommand("rhi" + newLineChar + newLineChar);
			sendCommand("rhb" + newLineChar);
			sendCommand("" + kanal + newLineChar);
			sendCommand(command + newLineChar + newLineChar);
			sendCommand("rhe" + newLineChar);

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				telnet.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void openShutter(int kanal) {
		doShutterAction(kanal, "up");
	}

	public void closeShutter(int kanal) {
		doShutterAction(kanal, "down");
	}

	public void stopShutter(int kanal) {
		doShutterAction(kanal, "stopp");
	}

	public static void main(String[] args) {
		try {
			HEYtechTelNetHelper telnet = new HEYtechTelNetHelper("10.0.1.6");
			System.out.println("Got Connection...");
			// telnet.getShutterStatus();
			int testKanal = 13;
			// telnet.closeShutter(testKanal);
			// telnet.openShutter(testKanal);
			// Thread.sleep(2000);
			// telnet.stopShutter(testKanal);
			System.out.println("DONE");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
