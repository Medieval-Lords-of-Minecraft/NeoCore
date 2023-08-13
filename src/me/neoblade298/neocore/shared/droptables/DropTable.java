package me.neoblade298.neocore.shared.droptables;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class DropTable<E> {
	private LinkedList<Droppable<E>> drops = new LinkedList<Droppable<E>>();
	private int totalWeight = 0;
	private static Random gen = new Random();
	
	public void add(E drop, int weight) {
		drops.add(new Droppable<E>(drop, weight));
		totalWeight += weight;
	}
	
	public E get() {
		int rand = gen.nextInt(totalWeight);
		
		Iterator<Droppable<E>> iter = rand > totalWeight / 2 ? drops.descendingIterator() : drops.iterator();
		Droppable<E> toReturn = null;
		while (rand > 0) {
			toReturn = iter.next();
			rand -= toReturn.getWeight();
		}
		
		return toReturn.get();
	}
}
