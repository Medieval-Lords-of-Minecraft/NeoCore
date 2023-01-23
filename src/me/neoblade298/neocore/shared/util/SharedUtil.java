package me.neoblade298.neocore.shared.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import me.neoblade298.neocore.bungee.BungeeCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class SharedUtil {
	public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
	private final static int CENTER_PX = 154;
	
	public static String center(String msg) {
		msg = translateColors(msg);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (char c : msg.toCharArray()) {
			if (c == '§') {
				previousCode = true;
			}
			else if (previousCode) {
				previousCode = false;
				isBold = (c == 'l' || c == 'L');
			}
			else {
				FontInfo fi = FontInfo.getFontInfo(c);
				messagePxSize += isBold ? fi.getBoldLength() : fi.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX - halvedMessageSize;
		int spaceLength = FontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + msg;
	}

	public static String translateColors(String textToTranslate) {

		Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
		StringBuffer buffer = new StringBuffer();

		while (matcher.find()) {
			matcher.appendReplacement(buffer, ChatColor.of(matcher.group(1)).toString());
		}

		return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
	}

	public static <T extends Comparable<T>> SortedMultiset<T> getTop(Collection<T> list, int num, boolean descending) {
		TreeMultiset<T> sorted = TreeMultiset.create();
		for (T item : list) {
			sorted.add(item);
			if (sorted.size() > num) {
				if (descending) {
					sorted.pollFirstEntry();
				}
				else {
					sorted.pollLastEntry();
				}
			}
		}
		return descending ? sorted.descendingMultiset() : sorted;
	}
	
	public static String connectArgs(String args[]) {
		return connectArgs(args, 0, args.length);
	}
	
	public static String connectArgs(String args[], int start) {
		return connectArgs(args, start, args.length);
	}
	
	public static String connectArgs(String args[], int start, int end) {
		String connected = "";
		for (int i = start; i < end; i++) {
			connected += args[i];
			if (i + 1 != end) {
				connected += " ";
			}
		}
		return connected;
	}
	
	public static void broadcast(String msg) {
		broadcast(msg, true);
	}
	
	public static void broadcast(String msg, boolean hasPrefix) {
		if (hasPrefix) {
			msg = "&4[&c&lMLMC&4] &7" + msg;
		}
		BungeeCore.inst().getProxy().broadcast(new TextComponent(SharedUtil.translateColors(msg)));
	}
}
