package me.neoblade298.neocore.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.Style.Merge.Strategy;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class SharedUtil {
	public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
	private final static int CENTER_PX = 154;
	private static final MiniMessage mini = MiniMessage.miniMessage();
	
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
		if (msg == null) return 0;
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
	
	private static ArrayList<String> addLineBreaks(String line, int pixelsPerLine) {
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
		LoreBuilder b = new LoreBuilder(pixelsPerLine);
	}
	
	private static void addLineBreaksHelper(TextComponent text, LoreBuilder b) {
		b.addStyleLayer(textye
			);
		addContent(text.content(), b);
	}
	
	private static void addContent(String content, LoreBuilder b) {
		for (char c : content.toCharArray()) {
			b.addChar(c);
		}
	}
	
	protected static class LoreBuilder {
		private StringBuilder b = new StringBuilder();
		private Stack<Style> parents = new Stack<Style>();
		private ArrayList<TextComponent> list = new ArrayList<TextComponent>();
		private Style currStyle;
		private TextComponent lineComponent;
		private int pixelsPerLine, linePixels = 0;
		private boolean isBold;
		
		protected LoreBuilder(int pixelsPerLine) {
			this.pixelsPerLine = pixelsPerLine;
			startNewComponent();
		}
		
		public void addStyleLayer(Component c) {
			parents.push(c.style());
			updateStyle();
		}
		
		public void removeStyleLayer() {
			parents.pop();
			updateStyle();
		}
		
		public ArrayList<TextComponent> finish() {
			endCurrentComponent();
			list.add(lineComponent);
			return list;
		}
		
		private void updateStyle() {
			currStyle = Style.empty();
			for (Style s : parents) {
				currStyle = currStyle.merge(s);
			}
		}
		
		public void replaceTopStyleLayer(Style s) {
			parents.pop();
			parents.push(s);
			updateStyle();

			startNewComponent();
		}
		
		public void addChar(char c) {
			FontInfo info = FontInfo.getFontInfo(c);
			linePixels += isBold ? info.getBoldLength() : info.getLength();
			
			if (c == ' ') {
				// If we need a new line now
				if (linePixels > pixelsPerLine) {
					startNewLine();
				}
				return;
			}
			
			b.append(c);
		}
		
		private void startNewComponent() {
			if (b.isEmpty()) return;
			endCurrentComponent();
			Builder b = Component.text();
			lineComponent = b.build();
		}
		
		private void startNewLine() {
			endCurrentComponent();
			list.add(lineComponent);
			startNewComponent();
			linePixels = 0;
		}
		
		private void endCurrentComponent() {
			TextComponent txt = Component.text().content(b.toString()).style(currStyle).build();
			lineComponent.append(txt);
		}
	}
	
	public static Component color(String msg) {
		return mini.deserialize(msg);
	}
	
	public static Component createText(String text, String hover, ClickEvent click) {
		Component c = SharedUtil.color(text);
		if (hover != null) {
			c = c.hoverEvent(HoverEvent.showText(SharedUtil.color(hover)));
		}
		if (click != null) {
			c = c.clickEvent(click);
		}
		return c;
	}
	
	public static Component createText(String text, Component hover, ClickEvent click) {
		Component c = SharedUtil.color(text);
		if (hover != null) {
			c = c.hoverEvent(HoverEvent.showText(hover));
		}
		if (click != null) {
			c = c.clickEvent(click);
		}
		return c;
	}
}
