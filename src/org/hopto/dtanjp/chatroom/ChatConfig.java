package org.hopto.dtanjp.chatroom;
import java.net.ServerSocket;
import java.net.Socket;

import org.hopto.dtanjp.chatroom.networking.ChatServer;
import org.hopto.dtanjp.chatroom.networking.Client;

/**
 * ChatConfig.java
 * 
 * @author David Tan
 **/
public class ChatConfig {

	/** Connection **/
	public static String HOST = "127.0.0.1";
	public static int PORT = 43594;
	public static Socket connection = null;
	public static ServerSocket server = null;
	
	public static String name = "";
	public static Client client = null;
	public static ChatServer chatServer = null;
	
	public static boolean isConnected() {
		if(connection == null)
			return false;
		return (!connection.isClosed() && connection.isConnected() && !connection.isOutputShutdown() && !connection.isInputShutdown());
	}
}
