package me.neoblade298.neocore.velocity.util;

import java.util.Collection;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import me.neoblade298.neocore.velocity.VelocityCore;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

public class Util {
	private static Component prefix;
	private static final Sound errorSound = 
			Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MUSIC, 1f, 0.7f);
	
	static {
		prefix = Component.text("[", NamedTextColor.DARK_RED)
				.append(Component.text("MLMC", NamedTextColor.RED, TextDecoration.BOLD))
				.append(Component.text("]")).appendSpace();
	}
	
	public static void msgGroup(Collection<CommandSource> s, Component msg, boolean hasPrefix) {
		for (CommandSource sender : s) {
			msg(sender, msg, hasPrefix);
		}
	}
	
	public static void msgGroup(Collection<CommandSource> s, Component msg) {
		msgGroup(s, msg, true);
	}
	
	public static void msgGroupRaw(Collection<CommandSource> s, Component msg) {
		msgGroup(s, msg, false);
	}
	
	public static void msgRaw(CommandSource s, String msg) {
		msg(s, VelocityCore.miniMessage().deserialize(msg), false);
	}
	
	public static void msgRaw(CommandSource s, Component msg) {
		msg(s, msg, false);
	}
	
	public static void msg(CommandSource s, String msg) {
		msg(s, VelocityCore.miniMessage().deserialize(msg), true);
	}
	
	public static void msg(CommandSource s, Component msg) {
		msg(s, msg, true);
	}
	
	public static void msg(CommandSource s, Component msg, boolean hasPrefix) {
		s.sendMessage(hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY));
	}
	
	public static void broadcastRaw(Component msg) {
		broadcast(msg, false);
	}
	
	public static void broadcast(Component msg) {
		broadcast(msg, true);
	}
	
	public static void broadcast(Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY);
		for (Player p : VelocityCore.proxy().getAllPlayers()) {
			p.sendMessage(msg);
		}
	}
	
	public static void mutableBroadcast(String tagForMute, Component msg) {
		mutableBroadcast(tagForMute, msg, true);
	}
	
	public static void mutableBroadcast(String tagForMute, Component msg, boolean hasPrefix) {
		msg = hasPrefix ? prefix.append(msg.colorIfAbsent(NamedTextColor.GRAY)) : msg.colorIfAbsent(NamedTextColor.GRAY);
		VelocityCore.sendPluginMessage(new String[] {"mutablebc", tagForMute, JSONComponentSerializer.json().serialize(msg)});
	}
	
	public static void displayError(Player p, String error) {
		p.playSound(errorSound);
		Util.msgRaw(p, Component.text(error, NamedTextColor.RED));
	}
}
