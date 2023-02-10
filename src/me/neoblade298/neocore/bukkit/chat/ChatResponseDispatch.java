package me.neoblade298.neocore.bukkit.chat;

public class ChatResponseDispatch {
	private String id;
	private ChatResponseHandler handler;
	public ChatResponseDispatch(String id, ChatResponseHandler handler) {
		this.id = id;
		this.handler = handler;
	}
	public String getId() {
		return id;
	}
	public ChatResponseHandler getHandler() {
		return handler;
	}
}
