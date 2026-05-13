package br.com.pereiraeng.network.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import br.com.pereiraeng.core.Flow;
import br.com.pereiraeng.icons.Icons;
import br.com.pereiraeng.icons.PereiraIcon;
import br.com.pereiraeng.swing.App;
import br.com.pereiraeng.swing.button.SemiToggleButton;
import br.com.pereiraeng.swing.input.IPinput;
import br.com.pereiraeng.swing.time.ClockPanel;
import br.com.pereiraeng.swing.time.TimeEvent;
import br.com.pereiraeng.swing.time.TimeListener;

public abstract class AbstractClient<K> extends JPanel implements App, ActionListener, TimeListener, Runnable, Flow<K> {
	private static final long serialVersionUID = 1L;

	protected final String user, preffix;

	protected final int ip0, port0;

	// parte gráfica

	private JFrame frame2;

	protected ClockPanel clock;

	protected SemiToggleButton syncAssync;

	protected IPinput ipInput;

	public AbstractClient(ClockPanel clock, String user, String preffix, int ip0, int port0) {
		this.clock = clock;
		this.user = user;
		this.preffix = preffix;
		this.ip0 = port0;
		this.port0 = port0;
	}

	@Override
	public void build(Component comp) {
		setLayout(new BorderLayout());

		if (comp instanceof JFrame) {
			this.frame2 = (JFrame) comp;
			this.frame2.setContentPane(this);
		}

		JPanel p0 = new JPanel();

		// relógio
		boolean flag = clock == null;
		if (flag) {
			// criar e instalar, se isso já não tiver sido feito em um programa
			// encapsulante
			this.clock = new ClockPanel();
			clock.setAutoTurnOff(false);
			p0.add(clock);
		}
		clock.addTimeListener(this);
		if (flag) // primeiro start
			this.clock.play();

		syncAssync = new SemiToggleButton(PereiraIcon.REFRESH.create());
		syncAssync.setPreferredSize(Icons.DIM_BUTTON_ICON);
		syncAssync.setToolTipText(
				"<html>Atualizar<br>(<strong>clique com o botão direito do mouse para atualizar a cada minuto</strong>)</html>");
		syncAssync.setToggleCommand("A");
		syncAssync.setActionCommand("R");
		syncAssync.addActionListener(this);
		p0.add(syncAssync);

		p0.add(ipInput = new IPinput(this.preffix, this.ip0, this.port0));

		this.add(p0, BorderLayout.NORTH);
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public boolean isMaximizable() {
		return true;
	}

	@Override
	public Dimension getWindowSize() {
		return new Dimension(800, 500);
	}

	@Override
	public void open(String file) {
	}

	@Override
	public void start() {
	}

	// ------------------------------- LISTENER -------------------------------

	protected transient boolean on = false;

	@Override
	public void actionPerformed(ActionEvent event) {
		char c0 = event.getActionCommand().charAt(0);

		boolean b = true;
		switch (c0) {
		case 'A': // síncrona (direito, liga ou desliga; se ligar, pede-se agora)
			this.on = ((SemiToggleButton) event.getSource()).isSelected();
			b = this.on;
		case 'R': // assíncrona (esquerdo, pede-se agora)
			if (b)
				new Thread(this).start();
			break;
		}
	}

	@Override
	public void timeElapsed(TimeEvent event) {
		if (syncAssync.isEnabled() && on)
			new Thread(this).start();
	}
}
