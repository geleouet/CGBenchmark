package fr.egaetan.locm;

public class OrderCarte {
	int turn;
	int best;
	int a;
	int b;

	@Override
	public String toString() {
		return best + " > " + a + ", " + b;
	}
}