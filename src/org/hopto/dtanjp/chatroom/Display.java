package org.hopto.dtanjp.chatroom;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.hopto.dtanjp.chatroom.api.state.StateManager;
/**
 * Display.java
 * 
 * @author David Tan
 **/
public class Display extends JPanel {

	/** Generated serial version UID **/
	private static final long serialVersionUID = 8274011568777903027L;

	/** Constructor **/
	private Display() {
		setLayout(null);
		setBorder(BorderFactory.createEtchedBorder(Color.CYAN, Color.GREEN));
		setBounds(50, 80, 700, 700);
	}
	
	public void paintComponent(Graphics graphics) {
		Graphics2D g = (Graphics2D)graphics;
		g.setColor(Color.CYAN);
		g.drawString("[ Chatroom ]", 5, 15);
		if(StateManager.getInstance().CurrentState() != null)
			StateManager.getInstance().CurrentState().Render(g);
	}
	
	/** Singleton **/
	public static Display getInstance() {
		if(instance == null)
			instance = new Display();
		return instance;
	}
	
	/** Variables **/
	private static Display instance = null;
}
