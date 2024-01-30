package me.neoblade298.neocore.bukkit.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import me.neoblade298.neocore.bukkit.inventories.CoreInventory;

public class InventoryListener implements Listener {
	private static HashMap<Player, CoreInventory> invs = new HashMap<Player, CoreInventory>();
	
	public static void registerInventory(Player p, CoreInventory inv) {
		invs.put(p, inv);
	}
	
	public static CoreInventory getCoreInventory(Player p) {
		return invs.get(p);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (invs.containsKey(p)) {
			invs.get(p).handleInventoryClick(e);
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (invs.containsKey(p)) {
			invs.get(p).handleInventoryDrag(e);
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		if (invs.containsKey(p) && e.getInventory() == invs.get(p).getInventory()) {
			invs.get(p).handleInventoryClose(e);
			invs.remove(p);
		}
	}
}
