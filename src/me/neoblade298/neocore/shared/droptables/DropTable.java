package me.neoblade298.neocore.shared.droptables;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class DropTable<E> {
	private TreeMap<Double, ArrayList<E>> drops = new TreeMap<Double, ArrayList<E>>();
	private double totalWeight = 0;
	private int size = 0;
	private static Random gen = new Random();
	
	public static DropTable<DropTable<?>> combine(DropTable<?>[] tables, double[] multipliers) {
		DropTable<DropTable<?>> group = new DropTable<DropTable<?>>();
		for (int i = 0; i < tables.length; i++) {
			group.add(tables[i], tables[i].getTotalWeight() * multipliers[i]);
		}
		return group;
	}
	
	public static DropTable<DropTable<?>> combine(DropTable<?>[] tables) {
		DropTable<DropTable<?>> group = new DropTable<DropTable<?>>();
		for (int i = 0; i < tables.length; i++) {
			group.add(tables[i], tables[i].getTotalWeight());
		}
		return group;
	}
	
	public void add(E drop, double weight) {
		ArrayList<E> weightList = drops.getOrDefault(weight, new ArrayList<E>());
		weightList.add(drop);
		drops.put(weight, weightList);
		totalWeight += weight;
		size++;
	}
	
	public E get() {
		double rand = gen.nextDouble() * totalWeight;
		
		ArrayList<E> list = null;
		double weight = -1;
		for (double w : drops.descendingKeySet()) {
			list = drops.get(w);
			weight = w;
			double listWeight = drops.get(weight).size() * weight;
			if (rand < drops.get(weight).size() * weight) {
				break;
			}
			rand -= listWeight;
		}
		return list.get((int) (rand / weight));
	}
	
	public boolean remove(E item, double weight) {
		boolean success = drops.get(weight).remove(item);
		if (success) totalWeight -= weight;
		return success;
	}
	
	public int remove(E item) {
		int count = 0;
		for (Entry<Double, ArrayList<E>> ent : drops.entrySet()) {
			if (ent.getValue().remove(item)) {
				count++;
				totalWeight -= ent.getKey();
			}
		}
		return count;
	}
	
	/*
	public DropTable<E> combine(DropTable<E>[] others) {
		double[] multipliers = new double[others.length];
		for (int i = 0; i < others.length; i++) {
			multipliers[i] = 1;
		}
		return combine(others, multipliers);
	}
	
	public DropTable<E> combine(DropTable<E>[] others, double[] multipliers) {
		DropTable<E> combined = new DropTable<E>();
		combined.drops.addAll(this.drops);
		
		for (Droppable<E> drop : this.drops) {
			combined.add(drop.get(), drop.getWeight());
		}
		int i = 0;
		for (DropTable<E> other : others) {
			for (Droppable<E> drop : other.drops) {
				combined.add(drop.get(), drop.getWeight() * multipliers[i++]);
			}
		}
		return combined;
	}*/
	
	public double getTotalWeight() {
		return totalWeight;
	}
	
	public int size() {
		return size;
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
}
