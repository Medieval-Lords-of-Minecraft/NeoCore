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
	
	public static ItemStack createButton(ItemStack item, String name, String... lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(SharedUtil.translateColors(name));
		ArrayList<String> list = new ArrayList<String>();
		for (String line : lore) {
			list.add(SharedUtil.translateColors(line));
		}
		meta.setLore(list);
		item.setItemMeta(meta);
		return item;
	}
	public void openInventory() {
		p.openInventory(inv);
		InventoryListener.registerInventory(p, this);
	}
}
