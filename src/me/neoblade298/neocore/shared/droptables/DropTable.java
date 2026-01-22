package me.neoblade298.neocore.shared.droptables;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.bukkit.Bukkit;

public class DropTable<E> {
	private TreeMap<Double, ArrayList<E>> drops = new TreeMap<Double, ArrayList<E>>();
	private double totalWeight = 0;
	private int size = 0;
	private static Random gen = new Random();

	public DropTable<E> combine(DropTable<E>[] others) {
		// Start with a clone of this table to avoid modifying the original
		DropTable<E> combined = this.clone();
		
		// Add all entries from other tables
		for (DropTable<E> other : others) {
			if (other == null) continue;
			
			// Iterate through each weight group in the other table
			for (Entry<Double, ArrayList<E>> entry : other.drops.entrySet()) {
				double weight = entry.getKey();
				ArrayList<E> items = entry.getValue();
				
				// Add each item with its weight to the combined table
				for (E item : items) {
					combined.add(item, weight);
				}
			}
		}
		
		return combined;
	}
	
	public DropTable() {}
	
	private DropTable(DropTable<E> original) {
		for (Entry<Double, ArrayList<E>> ent : original.drops.entrySet()) {
			drops.put(ent.getKey(), new ArrayList<E>(ent.getValue()));
		}
		totalWeight = original.totalWeight;
		size = original.size;
	}
	
	public DropTable<E> clone() {
		return new DropTable<E>(this);
	}
	
	public void add(E drop, double weight) {
		ArrayList<E> weightList = drops.getOrDefault(weight, new ArrayList<E>());
		weightList.add(drop);
		drops.put(weight, weightList);
		totalWeight += weight;
		size++;
	}
	
	public E get() {
		return getMultiple(1, false, null).get(0);
	}

	public ArrayList<E> getMultiple(int amount, boolean unique) {
		return getMultiple(amount, unique, null);
	}

	public ArrayList<E> getMultiple(int amount, boolean unique, ArrayList<E> blockedItems) {
		ArrayList<DroptableItem> removedItems = new ArrayList<DroptableItem>();
		// Temporarily remove blocked items from droptable
		if (blockedItems != null) {
			for (E item : blockedItems) {
				for (Entry<Double, ArrayList<E>> ent : drops.entrySet()) {
					if (remove(item, ent.getKey())) {
						removedItems.add(new DroptableItem(item, ent.getKey()));
					}
				}
			}
		}
		
		ArrayList<E> results = new ArrayList<E>();
		for (int i = 0; i < amount; i++) {
			// Get one item at random
			double rand = gen.nextDouble() * totalWeight;
			ArrayList<E> list = null;
			double weight = -1;
			// Go through weight list from highest to lowest and find appropriate list
			for (double w : drops.descendingKeySet()) {
				list = drops.get(w);
				if (list.size() == 0) continue;
				weight = w;
				double listWeight = drops.get(weight).size() * weight;
				if (rand < drops.get(weight).size() * weight) {
					break;
				}
				rand -= listWeight;
			}
			if (list == null) {
				Bukkit.getLogger().warning("[NeoCore] Failed to get item from droptable with weight " + weight + ": " + this.toString());
				return null;
			}
			E item = list.get((int) (rand / weight));
			results.add(item);

			// Remove the item from the droptable
			if (unique) {
				if (remove(item, weight)) {
					removedItems.add(new DroptableItem(item, weight));
				}
			}
		}

		// Re-add removed items
		for (DroptableItem ditem : removedItems) {
			add(ditem.item, ditem.weight);
		}
		return results;
	}
	
	public boolean remove(E item, double weight) {
		boolean success = drops.get(weight).remove(item);
		if (success) {
			totalWeight -= weight;
			size--;
		}
		return success;
	}
	
	public int remove(E item) {
		int count = 0;
		for (Entry<Double, ArrayList<E>> ent : drops.entrySet()) {
			if (ent.getValue().remove(item)) {
				count++;
				totalWeight -= ent.getKey();
				size--;
			}
		}
		return count;
	}
	
	public double getTotalWeight() {
		return totalWeight;
	}
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}
	
	public ArrayList<E> getItems() {
		ArrayList<E> items = new ArrayList<E>(size);
		for (ArrayList<E> list : drops.values()) {
			items.addAll(list);
		}
		return items;
	}
	
	@Override
	public String toString() {
		return drops.toString();
	}

	private class DroptableItem {
		public E item;
		public double weight;
		
		public DroptableItem(E item, double weight) {
			this.item = item;
			this.weight = weight;
		}
	}
}
