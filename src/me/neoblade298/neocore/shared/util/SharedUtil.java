package me.neoblade298.neocore.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import net.md_5.bungee.api.ChatColor;

public class SharedUtil {
	public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
	private final static int CENTER_PX = 154;
	
	public static String center(String msg) {
		int messagePxSize = getStringPixels(msg);
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
	
	public static int getStringPixels(String msg) {
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
		return messagePxSize;
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
	
	public static ArrayList<String> addLineBreaks(String line, int pixelsPerLine, ChatColor color) {
		ArrayList<String> lines = new ArrayList<String>();
		String[] words = line.split(" ");
		String curr = color.toString();
		int linePixels = 0;
		for (String word : words) {
			int pixels = getStringPixels(word);
			if (linePixels == 0) {
				curr += word;
				linePixels += pixels;
			}
			else if (linePixels + pixels + FontInfo.getFontInfo(' ').getLength() > pixelsPerLine) {
				lines.add(curr);
				curr = color + word;
				linePixels = pixels;
			}
			else {
				curr += " " + word;
				linePixels += pixels + FontInfo.getFontInfo(' ').getLength();
			}
		}
		lines.add(curr);
		return lines;
	}
}
