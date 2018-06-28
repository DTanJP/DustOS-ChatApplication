package org.hopto.dtanjp.chatroom.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.hopto.dtanjp.chatroom.ChatConfig;

public class Client extends Thread {

	public Client() throws IOException {
		this.socket = ChatConfig.connection;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	/** Constructor @throws IOException **/
	public Client(Socket socket) throws IOException {
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}
	
	@Override
	public void run() {
		String line = "";
		while((line = readIO()) != null) {
			if(ChatConfig.chatServer != null)
				ChatConfig.chatServer.messageAll(line);
		}
		System.out.println(socket.getRemoteSocketAddress()+" disconnected.");
	}
	
	public boolean isConnected() {
		if(socket == null)
			return false;
		if(socket.isClosed() || !socket.isConnected())
			return false;
		return true;
	}
	
	public String readIO() {
		try {
			return in.readLine();
		} catch (IOException e) {
			return null;
		}
	}
	
	public void writeIO(Object data) {
		out.println(data);
	}
	
	public boolean serverOffline() {
		out.println("");
		return out.checkError();
	}
	
	public void disconnect() {
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(out != null)
			out.close();
	}
	
	/** Variables **/
	public final Socket socket;
	public final PrintWriter out;//Sends out data
	public final BufferedReader in;//Reads in data
}
