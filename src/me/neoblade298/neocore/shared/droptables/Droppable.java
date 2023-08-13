package me.neoblade298.neocore.shared.droptables;

public class Droppable<E> {
	private int weight;
	private E drop;
	
	public Droppable(E drop) {
		this.drop = drop;
	}
	
	public Droppable(E drop, int weight) {
		this.drop = drop;
		this.weight = weight;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public E get() {
		return drop;
	}
}
