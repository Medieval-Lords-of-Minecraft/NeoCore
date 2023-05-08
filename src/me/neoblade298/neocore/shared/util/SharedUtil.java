package me.neoblade298.neocore.shared.util;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.hover.content.Text;

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
	
	public static String connectArgs(String args[], char delimiter) {
		return connectArgs(args, 0, args.length, delimiter);
	}
	
	public static String connectArgs(String args[]) {
		return connectArgs(args, 0, args.length, ' ');
	}
	
	public static String connectArgs(String args[], int start) {
		return connectArgs(args, start, args.length, ' ');
	}
	
	public static String connectArgs(String args[], int start, int end) {
		return connectArgs(args, start, end, ' ');
	}
	
	public static String connectArgs(String args[], int start, int end, char delimiter) {
		String connected = "";
		for (int i = start; i < end; i++) {
			connected += args[i];
			if (i + 1 != end) {
				connected += delimiter;
			}
		}
		return connected;
	}
	
	public static boolean isNumeric(String in) {
		return StringUtils.isNumeric(in);
	}
	
	public static ComponentBuilder createText(String text, String hover, String cmd) {
		ComponentBuilder b = new ComponentBuilder(SharedUtil.translateColors(text));
		if (hover != null) {
			b.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(SharedUtil.translateColors(hover))));
		}
		if (cmd != null) {
			b.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		}
		return b;
	}
	
	public static ComponentBuilder appendText(ComponentBuilder b, String text, String hover, String cmd) {
		b.append(SharedUtil.translateColors(text), FormatRetention.NONE);
		if (hover != null) {
			b.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(SharedUtil.translateColors(hover))));
		}
		if (cmd != null) {
			b.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		}
		return b;
	}
}
