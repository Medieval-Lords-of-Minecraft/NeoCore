package me.neoblade298.neocore.shared.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
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
	
	public static ArrayList<TextComponent> addLineBreaks(TextComponent text, int pixelsPerLine) {
		LoreBuilder b = new LoreBuilder(pixelsPerLine);
		addLineBreaksHelper(text, b);
		return b.finish();
	}
	
	private static void addLineBreaksHelper(TextComponent text, LoreBuilder b) {
		b.pushStyle(text);
		
		b.startNewComponent(text);
		for (char c : text.content().toCharArray()) {
			b.addChar(c);
		}
		b.endCurrentComponent();
		
		for (Component c : text.children()) {
			addLineBreaksHelper((TextComponent) c, b);
		}
		b.popStyle(text);
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
			lineComponent = Component.text().build();
		}
		
		public ArrayList<TextComponent> finish() {
			if (linePixels == 0) return list;

			endLine();
			return list;
		}
		
		private void updateStyle() {
			currStyle = Style.empty();
			for (Style s : parents) {
				currStyle = currStyle.merge(s);
			}
		}
		
		public void addChar(char c) {
			FontInfo info = FontInfo.getFontInfo(c);
			linePixels += isBold ? info.getBoldLength() : info.getLength();
			
			if (c == ' ') {
				// If we need a new line now
				if (linePixels > pixelsPerLine) {
					endLine();
					return;
				}
			}
			
			b.append(c);
		}
		
		public void pushStyle(Component c) {
			parents.push(c.style());
			updateStyle();
		}
		
		public void popStyle(Component c) {
			parents.pop();
			updateStyle();
		}
		
		public void startNewComponent(Component c) {
			b = new StringBuilder();
		}
		
		private void endCurrentComponent() {
			TextComponent txt = Component.text().content(b.toString()).style(currStyle).build();
			b = new StringBuilder();
			lineComponent = lineComponent.append(txt);
		}
		
		private void endLine() {
			// Append last line in builder
			TextComponent txt = Component.text().content(b.toString()).build();
			lineComponent = lineComponent.append(txt);
			
			// Create new line in list
			list.add(lineComponent);
			
			// Restart builder
			b = new StringBuilder();
			lineComponent = Component.text().build();
			linePixels = 0;
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
