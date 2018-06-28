package org.hopto.dtanjp.chatroom.networking;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hopto.dtanjp.chatroom.ChatConfig;
import org.hopto.dtanjp.chatroom.MainApp;

public class ChatServer implements Runnable {

	/** Constructor **/
	private ChatServer() {
		users = new ArrayList<>();
	}
	
	@Override
	public void run() {
		System.out.println("[ChatServer]: Starting ChatServer: "+ChatConfig.server.getInetAddress());
		running = true;
		while(running) {
			try {
				Socket socket = ChatConfig.server.accept();
				Client client = new Client(socket);
				if(client.isConnected()) {
					socket.setTcpNoDelay(true);
					socket.setKeepAlive(true);
					client.start();
					users.add(client);
					System.out.println("[ChatServer]: Accepted client: "+socket.getRemoteSocketAddress());
				} else {
					client.disconnect();
					System.out.println("[ChatServer]: Denied client: "+socket.getRemoteSocketAddress());
				}
				//Kick null users
				users.stream()
				.filter(c -> c == null)
				.forEach(c -> users.remove(c));
				
				//Kick inactive users
				users.stream()
				.filter(c -> !c.isAlive() || !c.isConnected())
				.forEach(c -> {
					c.disconnect();
					System.out.println("[ChatServer]: Removing inactive client: "+c.socket.getRemoteSocketAddress());
					users.remove(c);
				});
			} catch (IOException e) {
				System.out.println("[ChatServer]: Server refuses to accept sockets.");
			}
		}
			System.out.println("[ChatServer]: Server is shutting down.");
			for(Client client : users)
				client.disconnect();
			users.clear();
			users = null;
			instance = null;
			ChatConfig.chatServer = null;
			MainApp.instance.menu.SetCurrentState();
	}
	
	public Client connect(Socket socket) {
		try {
			Client client = new Client(socket);
			if(client.isConnected()) {
				socket.setTcpNoDelay(true);
				socket.setKeepAlive(true);
				((Thread)client).setDaemon(true);
				((Thread)client).start();
				users.add(client);
				System.out.println("[ChatServer]: Accepting HOST: "+socket.getRemoteSocketAddress());
				return client;
			} else {
				client.disconnect();
				System.out.println("[ChatServer]: Denied HOST: "+socket.getRemoteSocketAddress());
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void messageAll(String line) {
//		for(Client client : users) {
//			try {
//				PrintWriter out = new PrintWriter(client.socket.getOutputStream(), true);
//				out.println(line);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		synchronized(users) {
			Iterator<Client> user = users.iterator();
			while(user.hasNext()) {
				try {
					PrintWriter out = new PrintWriter(user.next().socket.getOutputStream(), true);
					out.println(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public boolean isOnline() {
		if(ChatConfig.server == null || ChatConfig.chatServer == null)
			return false;
		return (!ChatConfig.server.isClosed());
	}
	
	public void shutdown() {
		System.out.println("[ChatServer]: shutdown()");
		running = false;
	}
	
	/** Singleton **/
	public static ChatServer getInstance() {
		if(instance == null)
			instance = new ChatServer();
		return instance;
	}
	
	/** Variables **/
	private List<Client> users;
	private static ChatServer instance = null;
	public boolean running = false;
}
