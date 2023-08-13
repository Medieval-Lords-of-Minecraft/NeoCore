package me.neoblade298.neocore.shared.droptables;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class DropTable<E extends Droppable> {
	private LinkedList<E> drops = new LinkedList<E>();
	private int totalWeight = 0;
	private static Random gen = new Random();
	
	public void add(E drop) {
		drops.add(drop);
		totalWeight += drop.getWeight();
	}
	
	public E get() {
		int rand = gen.nextInt(totalWeight);
		
		Iterator<E> iter = rand > totalWeight / 2 ? drops.descendingIterator() : drops.iterator();
		E toReturn = null;
		while (rand > 0) {
			toReturn = iter.next();
			rand -= toReturn.getWeight();
		}
		
		return toReturn;
	}
}
