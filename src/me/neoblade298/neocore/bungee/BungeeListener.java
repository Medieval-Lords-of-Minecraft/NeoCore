package me.neoblade298.neocore.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

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
		Bukkit.getLogger().info("Read message " + subchannel + " " + msg);
		
		// Pre-read plugin message for any neocore messages, return true if the message was handled
		if (!readPluginMessage(subchannel, msgs)) {
			Bukkit.getPluginManager().callEvent(new PluginMessageEvent(subchannel, msgs));
		}
	}
	
	private boolean readPluginMessage(String subchannel, ArrayList<String> msgs) {
		if (subchannel.equals("neocore-tp")) {
			UUID src = UUID.fromString(msgs.get(0));
			UUID trg = UUID.fromString(msgs.get(1));
			Player playerSrc = Bukkit.getPlayer(src);
			Player playerTrg = Bukkit.getPlayer(trg);
			
			playerSrc.teleport(playerTrg);
			return true;
		}
		return false;
	}
}
