package me.neoblade298.neocore.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;

public class SharedUtil {
	public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
	private final static int CENTER_PX = 154;
	
	public static String center(String msg) {
		int messagePxSize = getStringPixels(msg, false);
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
	
	public static int getStringPixels(String msg, boolean isBold) {
		int messagePxSize = 0;

		for (char c : msg.toCharArray()) {
			FontInfo fi = FontInfo.getFontInfo(c);
			messagePxSize += isBold ? fi.getBoldLength() : fi.getLength();
			messagePxSize++;
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
	
	public static ArrayList<String> addLineBreaks(String line, int pixelsPerLine) {
		ArrayList<String> lines = new ArrayList<String>();
		String[] words = line.split(" ");
		String curr = "";
		int linePixels = 0;
		for (String word : words) {
			int pixels = getStringPixels(word, false); // Doesn't support bold
			if (linePixels == 0) {
				curr += word;
				linePixels += pixels;
			}
			else if (linePixels + pixels + FontInfo.getFontInfo(' ').getLength() > pixelsPerLine) {
				lines.add(curr);
				curr = word;
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
	
	public static ArrayList<TextComponent> addLineBreaks(TextComponent text, int pixelsPerLine) {
		ArrayList<TextComponent> list = new ArrayList<TextComponent>();
		addLineBreaksComponent(text, list, 250, new LineMetadata());
		return list;
	}
	
	private static void addLineBreaksComponent(TextComponent tc, ArrayList<TextComponent> list, int pixelsPerLine, LineMetadata data) {
		// Get list of text decorations in the component
		ArrayList<TextDecoration> temp = new ArrayList<TextDecoration>();
		for (Entry<TextDecoration, State> e : tc.decorations().entrySet()) {
			if (e.getValue() == State.TRUE) temp.add(e.getKey());
		}
		TextDecoration[] decor = new TextDecoration[temp.size()];
		int idx = 0;
		for (TextDecoration d : temp) {
			decor[idx++] = d;
		}
		
		boolean bold = tc.decoration(TextDecoration.BOLD) == State.TRUE;
		System.out.println("New component content: " + tc.content());
		System.out.println("===" + pixelsPerLine);
		
		if (!tc.content().isEmpty()) {
			for (String word : tc.content().split(" ")) {
				System.out.println("Word: " + word);
				int pixels = getStringPixels(word, bold);
				
				if (data.linePixels == 0) {
					data.b.append(word);
					data.linePixels += pixels;
					System.out.println("Word 1: " + data.b.toString() + " " + data.linePixels);
				}
				else if (data.linePixels + pixels + FontInfo.getFontInfo(' ').getLength() > pixelsPerLine) {
					list.add(Component.text(data.getString(), tc.color()));
					System.out.println("Add line: " + list.get(list.size() - 1));
					data.linePixels = pixels;
				}
				else {
					data.b.append(' ').append(word);
					data.linePixels += pixels + FontInfo.getFontInfo(' ').getLength();
				}
			}
		}
		
		for (Component child : tc.children()) {
			addLineBreaksComponent((TextComponent) child, list, pixelsPerLine, data);
		}
	}
	
	public static class LineMetadata {
		private StringBuilder b = new StringBuilder();
		private int linePixels = 0;
		
		public LineMetadata() {}
		
		public String getString() {
			String str = b.toString();
			b = new StringBuilder();
			return str;
		}
	}
}
