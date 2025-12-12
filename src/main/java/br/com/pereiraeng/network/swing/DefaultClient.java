package br.com.pereiraeng.network.swing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import br.com.pereiraeng.network.ConnWS;
import br.com.pereiraeng.swing.time.ClockPanel;

public abstract class DefaultClient extends AbstractClient<String> {
	private static final long serialVersionUID = -3210914751953533152L;

	public DefaultClient(ClockPanel clock, String user, String preffix, int ip0, int port0) {
		super(clock, user, preffix, ip0, port0);
	}

	// ---------------- RUNNABLE & NETWORKING ----------------

	protected ConnWS conn;

	@Override
	public void run() {
		try {
			String ip = super.ipInput.getIP();
			int port = super.ipInput.getPort();
			System.out.println(ip + "\t" + port);
			Socket client = new Socket(InetAddress.getByName(ip), port);
			conn = new ConnWS(client);
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
				this.conn.writeObject(""); // TODO
			} catch (IOException e) {
				e.printStackTrace();
			}

			conn.close();

			syncAssync.setEnabled(true);
		}
	}

}
