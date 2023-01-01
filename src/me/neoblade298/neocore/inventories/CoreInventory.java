package me.neoblade298.neocore.inventories;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public abstract class CoreInventory {
	protected Inventory inv;
	protected Player p;
	public abstract void handleInventoryClick(InventoryClickEvent e);
	public abstract void handleInventoryDrag(InventoryDragEvent e);
	public abstract void handleInventoryClose(InventoryCloseEvent e);
	public CoreInventory(Player p, Inventory inv) {
		this.inv = inv;
		this.p = p;
		p.openInventory(inv);
		InventoryListener.registerInventory(p, this);
	}
	public Inventory getInventory() {
		return inv;
	}
	public Player getPlayer() {
		return p;
	}
}
