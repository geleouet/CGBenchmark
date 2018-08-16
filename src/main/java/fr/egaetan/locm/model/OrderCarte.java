package fr.egaetan.locm.model;

public class OrderCarte {
	public int turn;
	public int best;
	public int a;
	public int b;

	@Override
	public String toString() {
		return best + " > " + a + ", " + b;
	}
}