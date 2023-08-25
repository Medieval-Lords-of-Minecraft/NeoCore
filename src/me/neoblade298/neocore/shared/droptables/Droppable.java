package me.neoblade298.neocore.shared.droptables;

public class Droppable<E> {
	private double weight;
	private E drop;
	
	public Droppable(E drop) {
		this.drop = drop;
	}
	
	public Droppable(E drop, double weight) {
		this.drop = drop;
		this.weight = weight;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public E get() {
		return drop;
	}
}
