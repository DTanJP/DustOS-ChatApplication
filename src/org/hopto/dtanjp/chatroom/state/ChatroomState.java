package org.hopto.dtanjp.chatroom.state;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import org.hopto.dtanjp.chatroom.ChatConfig;
import org.hopto.dtanjp.chatroom.MainApp;
import org.hopto.dtanjp.chatroom.api.state.State;

public class ChatroomState extends State {

	public ChatroomState() {
		disconnect = new JButton("Disconnect");
		disconnect.setBounds(540, 15, 100, 30);
		disconnect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChatConfig.client.writeIO("<"+ChatConfig.name+"> has disconnected.");
				MainApp.instance.menu.SetCurrentState();
			}
			
		});
		cmdinput = new JTextField("");
		cmdinput.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		cmdinput.setBounds(25, 650, 630, 30);
		cmdinput.setBackground(Color.BLACK);
		cmdinput.setCaretColor(Color.GREEN);
		cmdinput.setSelectedTextColor(Color.WHITE);
		cmdinput.setDisabledTextColor(Color.GRAY);
		cmdinput.setSelectionColor(Color.BLUE);
		cmdinput.setForeground(Color.CYAN);
		cmdinput.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		
		messageBox = new JTextArea("");
		messageBox.setEditable(false);
		messageBox.setBackground(Color.BLUE);
		messageBox.setForeground(Color.CYAN);
		messageBox.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		messageBox.setBounds(25, 30, 630, 600);
		messageBox.setLineWrap(true);
		messageBox.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		messageBox.setAutoscrolls(true);
		
		scrollpane = new JScrollPane(messageBox, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setBackground(Color.BLACK);
		scrollpane.setForeground(Color.CYAN);
		scrollpane.setBorder(BorderFactory.createLineBorder(Color.CYAN));
		scrollpane.setAutoscrolls(true);
		scrollpane.setBounds(25, 30, 630, 600);
		scrollpane.setWheelScrollingEnabled(true);
		cmdinput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!cmdinput.getText().isEmpty())
					ChatConfig.client.writeIO("<"+ChatConfig.name+">: "+cmdinput.getText());
				cmdinput.setText("");
			}
		});
		AddComponent("disconnect", disconnect);
		AddComponent("scrollpane", scrollpane);
		AddComponent("cmdinput", cmdinput);
	}
	
	@Override
	public void Render(Graphics2D g) {
	}

	@Override
	public void Enter() {
		if(ChatConfig.client != null)
			ChatConfig.client.writeIO("Welcome "+ChatConfig.name+" to the server!");
	}
	
	@Override
	public void Update() {
		if(ChatConfig.client != null) {
			Println(ChatConfig.client.readIO());
			
			if(ChatConfig.chatServer == null) {
				if(ChatConfig.client.serverOffline())
					MainApp.instance.menu.SetCurrentState();
			}
		} else {
			if(!ChatConfig.isConnected() && ChatConfig.chatServer == null)
				MainApp.instance.menu.SetCurrentState();
		}
	}

	@Override
	public void Exit() {
		if(ChatConfig.connection != null) {
			try {
				ChatConfig.connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(ChatConfig.client != null)
			ChatConfig.client.disconnect();
		
		//Shut the server down
		if(ChatConfig.server != null) {
			try {
				ChatConfig.server.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(ChatConfig.chatServer != null)
			ChatConfig.chatServer.shutdown();
		
		ChatConfig.server = null;
		ChatConfig.connection = null;
		ChatConfig.client = null;
	}
	
	public void Println(String line) {
		if(line == null) return;
		if(line.isEmpty()) return;
		messageBox.append(" "+line+"\n");
		//Auto scroll to bottom of text area
		scrollpane.getVerticalScrollBar().setValue(scrollpane.getVerticalScrollBar().getMaximum());
	}
	
	/** Variables **/
	private JScrollPane scrollpane = null;
	public JTextArea messageBox = null;
	public JTextField cmdinput = null;
	private JButton disconnect = null;
}
