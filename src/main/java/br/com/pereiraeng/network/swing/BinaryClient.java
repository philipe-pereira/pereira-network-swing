package br.com.pereiraeng.network.swing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import br.com.pereiraeng.network.ConnS;
import br.com.pereiraeng.swing.time.ClockPanel;

public abstract class BinaryClient extends AbstractClient<byte[]> {
	private static final long serialVersionUID = -3210914751953533152L;

	public BinaryClient(ClockPanel clock, String user, String preffix, int ip0, int port0) {
		super(clock, user, preffix, ip0, port0);
	}

	// ---------------- RUNNABLE & NETWORKING ----------------

	protected ConnS conn;

	@Override
	public void run() {
		try {
			String ip = super.ipInput.getIP();
			int port = super.ipInput.getPort();
			System.out.println(ip + "\t" + port);
			Socket client = new Socket(InetAddress.getByName(ip), port);
			conn = new ConnS(client);
			conn.listen(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		clock.stop();
		if (conn != null) {
			// tchau!
			try {
				this.conn.writeObject(new byte[] { '\n' });
			} catch (IOException e) {
				e.printStackTrace();
			}

			conn.close();

			syncAssync.setEnabled(true);
		}
	}
}
