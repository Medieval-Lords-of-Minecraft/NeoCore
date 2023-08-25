package me.neoblade298.neocore.shared.droptables;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import io.lumine.mythic.core.drops.Drop;

public class DropTable<E> {
	private LinkedList<Droppable<E>> drops = new LinkedList<Droppable<E>>();
	private double totalWeight = 0;
	private static Random gen = new Random();
	
	public void add(E drop, double weight) {
		drops.add(new Droppable<E>(drop, weight));
		totalWeight += weight;
	}
	
	public E get() {
		double rand = gen.nextDouble() * totalWeight;
		
		Iterator<Droppable<E>> iter = rand > totalWeight / 2 ? drops.descendingIterator() : drops.iterator();
		Droppable<E> toReturn = null;
		while (rand > 0) {
			toReturn = iter.next();
			rand -= toReturn.getWeight();
		}
		
		return toReturn.get();
	}
	
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
	}
	
	public double getTotalWeight() {
		return totalWeight;
	}
	
	public int size() {
		return drops.size();
	}
	
	@Override
	public String toString() {
		return drops.toString();
	}
}
