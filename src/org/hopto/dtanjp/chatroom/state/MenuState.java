package org.hopto.dtanjp.chatroom.state;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JTextField;

import org.hopto.dtanjp.chatroom.ChatConfig;
import org.hopto.dtanjp.chatroom.MainApp;
import org.hopto.dtanjp.chatroom.api.state.State;
import org.hopto.dtanjp.chatroom.networking.ChatServer;
import org.hopto.dtanjp.chatroom.networking.Client;

public class MenuState extends State {

	public MenuState() {
		SERVER_IP = new JTextField(ChatConfig.HOST);
		SERVER_PORT = new JTextField("8080");
		NAME_INPUT = new JTextField("");
		
		SETNAME = new JButton("Set name");
		connectButton = new JButton("Connect");
		createButton = new JButton("Create Server");
		
		SERVER_IP.setBounds(50, 120, 100, 30);
		SERVER_PORT.setBounds(50, 160, 100, 30);
		connectButton.setBounds(50, 200, 100, 30);
		
		createButton.setBounds(50, 240, 150, 30);
		
		NAME_INPUT.setBounds(90, 50, 100, 30);
		SETNAME.setBounds(200, 50, 100, 30);
		
		SETNAME.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(NAME_INPUT.isEnabled()) {
					if(!NAME_INPUT.getText().isEmpty()) {
						NAME_INPUT.setEnabled(false);
						connectButton.setEnabled(true);
						createButton.setEnabled(true);
						ChatConfig.name = NAME_INPUT.getText();
					}
				} else {
					NAME_INPUT.setEnabled(true);
					connectButton.setEnabled(false);
					createButton.setEnabled(false);
					ChatConfig.name = "";
				}
			}
			
		});
		connectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(SERVER_PORT.getText().isEmpty() || SERVER_IP.getText().isEmpty())
					return;
				
				try {
					ChatConfig.HOST = SERVER_IP.getText();
					ChatConfig.PORT = Integer.parseInt(SERVER_PORT.getText());
					ChatConfig.connection = new Socket(ChatConfig.HOST, ChatConfig.PORT);
					ChatConfig.client = new Client();
					System.out.println("[Chatroom]: Connecting to room: "+ChatConfig.connection.getRemoteSocketAddress());
				} catch(IOException ex) {
					ex.printStackTrace();
					return;
				}
			}
			
		});
		createButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(SERVER_PORT.getText().isEmpty())
					return;
				
				try {
					ChatConfig.PORT = Integer.parseInt(SERVER_PORT.getText());
					ChatConfig.server = new ServerSocket(ChatConfig.PORT);
					ChatConfig.chatServer = ChatServer.getInstance();
					(new Thread(ChatConfig.chatServer)).start();
					ChatConfig.connection = new Socket("127.0.0.1", ChatConfig.PORT);
					ChatConfig.client = new Client();
				} catch(IOException ex) {
					ex.printStackTrace();
					return;
				}
				
			}
			
		});
		AddComponent("SETNAME", SETNAME);
		AddComponent("NAME",NAME_INPUT);
		AddComponent("IP",SERVER_IP);
		AddComponent("PORT",SERVER_PORT);
		AddComponent("CONNECT",connectButton);
		AddComponent("CREATE",createButton);
	}
	
	@Override
	public void Render(Graphics2D g) {
		g.setColor(Color.CYAN);
		g.drawString("Name:", 50, 60);
	}

	@Override
	public void Enter() {
		MainApp.instance.room = new ChatroomState();
		if(ChatConfig.connection != null) {
			try {
				ChatConfig.connection.close();
				while(!ChatConfig.connection.isClosed())
					System.out.println("[Chatroom]: Waiting for connections to shut down...");
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
				while(!ChatConfig.server.isClosed())
					System.out.println("[Chatroom]: Waiting for server to shut down...");
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

	@Override
	public void Update() {
		if(NAME_INPUT.isEnabled()) {
			connectButton.setEnabled(false);
			createButton.setEnabled(false);
			NAME_INPUT.setEnabled(true);
		}
		
		if(ChatConfig.isConnected())
			MainApp.instance.room.SetCurrentState();
	}

	@Override
	public void Exit() {
	}

	/** Variables **/
	private JTextField SERVER_IP = null, SERVER_PORT = null, NAME_INPUT = null;
	private JButton connectButton = null, createButton = null, SETNAME = null;
	
}
