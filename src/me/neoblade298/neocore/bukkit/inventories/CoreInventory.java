package me.neoblade298.neocore.bukkit.inventories;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.neoblade298.neocore.bukkit.listeners.InventoryListener;
import me.neoblade298.neocore.bukkit.util.SkullUtil;
import me.neoblade298.neocore.shared.util.SharedUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public abstract class CoreInventory {
	protected Inventory inv;
	protected Player p;
	public abstract void handleInventoryClick(InventoryClickEvent e);
	public abstract void handleInventoryDrag(InventoryDragEvent e);
	public abstract void handleInventoryClose(InventoryCloseEvent e);
	public CoreInventory(Player p, Inventory inv) {
		this.inv = inv;
		this.p = p;
		openInventory();
	}
	public Inventory getInventory() {
		return inv;
	}
	public Player getPlayer() {
		return p;
	}
	public static ItemStack createButton(String b64, String name, String... lore) {
		ItemStack item = SkullUtil.itemFromBase64(b64);
		return createButton(item, name, lore);
	}
	
	public static ItemStack createButton(Material mat, String name, String... lore) {
		ItemStack item = new ItemStack(mat);
		return createButton(item, name, lore);
	}
	
	public static ItemStack createButton(Material mat, String name, String lore, int pixelsPerLine, ChatColor color) {
		ItemStack item = new ItemStack(mat);
		return createButton(item, name, lore, pixelsPerLine, color);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack createButton(ItemStack item, Component name, Component... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.displayName(name);
		ArrayList<Component> list = new ArrayList<Component>();
		for (Component line : lore) {
			list.add(line);
		}
		meta.lore(list);
		item.setItemMeta(meta);
		return item;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack createButton(ItemStack item, Component name, Component lore, int pixelsPerLine, ChatColor color) {
		ItemMeta meta = item.getItemMeta();
		meta.displayName(SharedUtil.translateColors(name));
		meta.setLore(SharedUtil.addLineBreaks(SharedUtil.translateColors(lore), pixelsPerLine, color));
		item.setItemMeta(meta);
		return item;
	}
	
	public void openInventory() {
		p.openInventory(inv);
		InventoryListener.registerInventory(p, this);
	}
}
