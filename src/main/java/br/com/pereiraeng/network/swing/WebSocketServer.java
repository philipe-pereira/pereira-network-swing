package br.com.pereiraeng.network.swing;

import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JTextField;

import br.com.pereiraeng.network.ConnWS;
import br.com.pereiraeng.network.Connn;

public abstract class WebSocketServer extends AbstractServer<String> {
	private static final long serialVersionUID = 2895947563698479531L;

	public WebSocketServer(String name, int port) {
		super(name, port);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			JTextField field = (JTextField) e.getSource();
			String s = field.getText();
			super.console.append(s + "\n");
			field.setText("");
			incomingData(super.user + "_" + s);
		}
	}

	@Override
	public void incomingData(String data) {
		if (isEnd(data)) {
			Iterator<Entry<String, Connn<String>>> it = user2conn.entrySet().iterator();
			while (it.hasNext()) {
				Connn<String> c = it.next().getValue();
				try {
					c.writeObject("_P");
				} catch (IOException e) {
					it.remove();
				}
			}
			System.out.println("Usuários restantes: " + user2conn.size());
		} else {
			if (data.charAt(data.length() - 1) == 'P') {
				String[] ss = data.split("_");
				if (ss.length == 2)
					System.out.println("Usuário " + ss[0] + " ainda on-line");
			}
		}
	}

	@Override
	public void run() {
		try { // set up server to receive connections; process connections
			super.server = new ServerSocket(super.ipInput.getPort());
			// create ServerSocket
			super.user2conn = new HashMap<>();
			while (super.active) {
				try { // wait for a connection & get input & output streams
					log.append("Waiting for connection\n");
					// wait for connection to arrive
					ConnWS c = new ConnWS(server.accept());
					c.listen(this);
					// PRIMEIRA COISA QUE O SERVIDOR FAZ É DAR UM NOME NA TABELA
					String name = generateName();
					putConnn(name, c);
					c.writeObject(name);
				} catch (SocketException | EOFException eofException) {
					log.append("\nServer terminated connection\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		try {
			super.closeConns();

			active = false;

			if (server != null) {
				server.close();
				server = null;
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static boolean isEnd(String data) {
		byte[] bs = data.getBytes();
		if (bs.length == 4) {
			short b = ByteBuffer.wrap(bs).getShort();
			switch (b) {
			case 1007:
				// 1007 indicates that an endpoint is terminating the connection
				// because it has received data within a message that was not
				// consistent with the type of the message (e.g., non-UTF-8 [RFC3629]
				// data within a text message).
				return true;
			default:
				return false;
			}
		} else
			return false;
	}
}
