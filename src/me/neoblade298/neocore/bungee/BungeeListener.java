package me.neoblade298.neocore.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class BungeeListener implements PluginMessageListener {
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(msg);
		String subchannel = in.readUTF();
		if (subchannel.startsWith("UltraEconomy")) return;
		
		short len = in.readShort();
		byte[] msgbytes = new byte[len];
		in.readFully(msgbytes);

		DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
		ArrayList<String> msgs = new ArrayList<String>();
		
		// Limit messages to 10 just in case some error
		for (int i = 0; i < 10; i++) {
			try {
				msgs.add(msgin.readUTF());
			}
			catch (EOFException e) {
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Bukkit.getPluginManager().callEvent(new PluginMessageEvent(subchannel, msgs));
	}
}
