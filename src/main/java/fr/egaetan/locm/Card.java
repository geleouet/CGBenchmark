package fr.egaetan.locm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Card {
	public int cost;
	public int damage;
	public int health;
	public int playerHP;
	public int opponentHP;
	public int cardDraw;
	public CardType type;

	public boolean breakthrough;
	public boolean charge;
	public boolean drain;
	public boolean guard;
	public boolean lethal;
	public boolean ward;
	
	public String name;
	public int id;
	
	public Card(int cost, CardType type, int damage, int health, int playerHP, int opponentHP, int cardDraw, boolean breakthrough, boolean charge, boolean drain, boolean guard, boolean lethal, boolean ward) {
		super();
		this.type = type;
		this.cost = cost;
		this.damage = damage;
		this.health = health;
		this.playerHP = playerHP;
		this.opponentHP = opponentHP;
		this.cardDraw = cardDraw;
		this.breakthrough = breakthrough;
		this.charge = charge;
		this.drain = drain;
		this.guard = guard;
		this.lethal = lethal;
		this.ward = ward;
	}
	
	public static List<Card> readCards() throws FileNotFoundException {
		Scanner in = new Scanner(new FileInputStream(new File("cartes.txt")));
		List<Card> cartes = new ArrayList<>();

		while (in.hasNext()) {
			String line = in.nextLine();
			String[] split = line.split(";");
			int cost = Integer.parseInt(split[3].trim());
			int damage = Integer.parseInt(split[4].trim());
			int health = Integer.parseInt(split[5].trim());
			int playerHP = Integer.parseInt(split[7].trim());
			int opponentHP = Integer.parseInt(split[8].trim());
			int cardDraw = Integer.parseInt(split[9].trim());

			boolean breakthrough = split[6].contains("B");
			boolean charge = split[6].contains("C");
			boolean drain = split[6].contains("D");
			boolean guard = split[6].contains("G");
			boolean lethal = split[6].contains("L");
			boolean ward = split[6].contains("W");

			CardType type = split[2].trim().equals("creature") ? CardType.CREATURE : (split[2].trim().equals("itemGreen") ? CardType.GREEN : (split[2].trim().equals("itemRed") ? CardType.RED : CardType.BLUE));
			/*if (type != CardType.CREATURE) {
				continue;
			}*/

			Card c = new Card(cost, type, damage, health, playerHP, opponentHP, cardDraw, breakthrough, charge, drain, guard, lethal, ward);
			c.name = split[1].trim();
			c.id = Integer.parseInt(split[0].trim());

			cartes.add(c);
		}

		in.close();
		return cartes;
	}

}