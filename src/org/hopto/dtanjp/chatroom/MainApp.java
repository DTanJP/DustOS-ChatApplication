package org.hopto.dtanjp.chatroom;
import org.cardboard.dtanjp.Computer;
import org.hopto.dtanjp.chatroom.api.state.State;
import org.hopto.dtanjp.chatroom.api.state.StateManager;
import org.hopto.dtanjp.chatroom.state.ChatroomState;
import org.hopto.dtanjp.chatroom.state.MenuState;

import Plugin.Plugin;
/**
 * MainApp.java
 * The MainApp for the Chatroom plugin
 * 
 * @author David Tan
 **/
public class MainApp implements Plugin {

	@Override
	public void Destruct() {
		System.out.println("[Chatroom]: Destruct");
		menu.SetCurrentState();
	}

	@Override
	public void Initialize() {
	}

	@Override
	public void OnDisable() {
		
	}

	@Override
	public void OnEnable() {
		System.out.println("[Chatroom]: Enabled");
		instance = this;
		display = Display.getInstance();
		if(room == null)
			room = new ChatroomState();
		
		if(menu == null)
			menu = new MenuState();
		menu.SetCurrentState();
		Computer.getInstance().GetOS().Request(this, "OS", "add-component", "main-panel", display);
	}

	@Override
	public void Request(Plugin sender, String receiver, String command, Object... params) {
		
	}

	@Override
	public boolean RequestShutDown() {
		return false;
	}

	@Override
	public void Update() {
		if(StateManager.getInstance().CurrentState() != null)
			StateManager.getInstance().CurrentState().Update();
	}

	/** Variables **/
	public static MainApp instance = null;
	private Display display;
	public State menu;
	public ChatroomState room = null;
}
