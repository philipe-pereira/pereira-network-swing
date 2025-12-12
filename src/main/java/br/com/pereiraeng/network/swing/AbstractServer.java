package br.com.pereiraeng.network.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.ServerSocket;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import br.com.pereiraeng.icons.Icons;
import br.com.pereiraeng.io.flow.Flow;
import br.com.pereiraeng.network.Connn;
import br.com.pereiraeng.swing.button.ChangeableButton;
import br.com.pereiraeng.swing.image.Pointer;
import br.com.pereiraeng.swing.input.IPinput;

public abstract class AbstractServer<K> extends JPanel implements Runnable, Flow<K>, KeyListener, ActionListener {
	private static final long serialVersionUID = -8708609540524131239L;

	protected static final String PREFIX = "_N";

	protected final String name;

	protected String user;

	protected IPinput ipInput;

	protected JProgressBar pb;

	protected JTextField input;
	protected JTextArea log, console;

	/**
	 * painel norte
	 */
	protected JPanel p0;

	public AbstractServer(String name, int port) {
		this(name, null, port);
	}

	public AbstractServer(String name, String ipv4, int port) {
		super(new BorderLayout());
		this.name = name;

		this.p0 = new JPanel();

		if (ipv4 == null)
			p0.add(ipInput = new IPinput(port));
		else {
			int p = ipv4.lastIndexOf('.') + 1;
			p0.add(ipInput = new IPinput(ipv4.substring(0, p), Integer.parseInt(ipv4.substring(p)), port));
		}
		
		ChangeableButton onOff = new ChangeableButton(new Icon[] { new Pointer(Pointer.BAD), new Pointer(Pointer.OK) });
		onOff.setActionCommand("S");
		onOff.setToolTipText("Ligar/desligar servidor");
		onOff.addActionListener(this);
		p0.add(onOff);

		JButton b = new JButton(Icons.loadUtilsIcon("clear.png"));
		b.addActionListener(this);
		b.setActionCommand("C");
		b.setToolTipText("Limpar log");
		p0.add(b);

		add(p0, BorderLayout.NORTH);

		// ----------------------------------------

		JSplitPane sp = new JSplitPane();
		this.log = new JTextArea();
		Font f = new Font("Sans-Serif", log.getFont().getStyle(), log.getFont().getSize());
		log.setFont(f);
		log.setEditable(false);
		log.setWrapStyleWord(true);
		log.setLineWrap(true);
		sp.setLeftComponent(new JScrollPane(this.log));

		JPanel p0 = new JPanel(new BorderLayout());
		this.console = new JTextArea();
		console.setEditable(false);
		console.setFont(f);
		console.setWrapStyleWord(true);
		console.setLineWrap(true);
		p0.add(new JScrollPane(console), BorderLayout.CENTER);
		p0.add(input = new JTextField(), BorderLayout.SOUTH);
		input.addKeyListener(this);
		sp.setRightComponent(p0);

		sp.setDividerLocation(400);

		add(sp, BorderLayout.CENTER);

		add(this.pb = new JProgressBar(), BorderLayout.SOUTH);
	}

	public void start(String user) {
		this.user = user;
	}

	public abstract void close();

	// ------------------- LISTENER -------------------

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		char c = e.getActionCommand().charAt(0);
		if (c == 'S') { // on/off
			active = ((ChangeableButton) e.getSource()).getSelected() == 1;
			if (active)
				new Thread(this).start();
			else
				close();
		} else if (c == 'C') { // limpar log
			log.setText("");
			console.setText("");
		}
	}

	// ------------------- RUNNABLE -------------------

	protected transient boolean active = false;

	/**
	 * server socket
	 */
	protected ServerSocket server;

	protected static transient int counter = 0;

	protected static String generateName() {
		int num = counter++;
		return PREFIX + num;
	}

	protected Map<String, Connn<K>> user2conn;

	protected void putConnn(String name, Connn<K> c) {
		user2conn.put(name, c);
		log.append("Cliente " + name + " conectado!\n");
	}

	public void closeConns() {
		if (user2conn != null) {
			for (Connn<K> c : user2conn.values())
				c.close();
			user2conn.clear();
			user2conn = null;
			counter = 0; // reset counter
		}
	}
}
