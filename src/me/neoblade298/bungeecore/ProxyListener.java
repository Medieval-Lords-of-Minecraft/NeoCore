package me.neoblade298.bungeecore;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyListener implements Listener {
	@EventHandler
	public void getPluginMessage(PluginMessageEvent e) {
		ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
		System.out.println(in.readUTF());
		System.out.println(in.readUTF());
		System.out.println(in.readUTF());
		System.out.println("Done");
	}
}
