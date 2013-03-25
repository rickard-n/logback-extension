package se.svt.logback.access;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

public class MockSyslogServer extends Thread {

	private final int loopLen;
	private final int port;

	private List<String> msgList = new ArrayList<String>();
	private boolean finished = false;

	public MockSyslogServer(int loopLen, int port) {
		super();
		this.loopLen = loopLen;
		this.port = port;
	}

	@Override
	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(port);

			for (int i = 0; i < loopLen; i++) {
				byte[] buf = new byte[1000 * 1024];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String msg = new String(buf, 0, packet.getLength());
				msgList.add(msg);
			}
		} catch (Exception se) {
			se.printStackTrace();
		} finally {
			if(socket != null) {
				try {socket.close();} catch(Exception e) {}
			}
		}
		finished = true;
	}

	public boolean isFinished() {
		return finished;
	}

	public List<String> getMessageList() {
		return msgList;
	}
}
