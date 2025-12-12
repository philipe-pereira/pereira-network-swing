package br.com.pereiraeng.network.swing;

import java.awt.event.KeyEvent;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;

import javax.swing.JTextField;

import br.com.pereiraeng.network.ConnM;
import br.com.pereiraeng.network.DefaultMsg;
import br.com.pereiraeng.network.Msg;

public abstract class SerializableServer<K extends Msg> extends AbstractServer<K> {
	private static final long serialVersionUID = 2895947563698479531L;

	public SerializableServer(String name, int port) {
		super(name, port);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			JTextField field = (JTextField) e.getSource();
			String s = field.getText();
			super.console.append(s + "\n");
			field.setText("");
			serverCommand(super.user + "_" + s);
		}
	}

	protected abstract void serverCommand(String command);

	@Override
	public void run() {
		try { // set up server to receive connections; process connections
			super.server = new ServerSocket(super.ipInput.getPort()); // create
			// ServerSocket
			user2conn = new HashMap<>();
			while (super.active) {
				try { // wait for a connection & get input & output streams
					log.append("Waiting for connection\n");
					// wait for connection to arrive
					ConnM<K> c = new ConnM<>(server.accept());
					c.listen(this);
					// PRIMEIRA COISA QUE O SERVIDOR FAZ É DAR UM NOME NA TABELA
					String name = generateName();
					putConnn(name, c);
					c.writeObject(new DefaultMsg(name));
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
}
