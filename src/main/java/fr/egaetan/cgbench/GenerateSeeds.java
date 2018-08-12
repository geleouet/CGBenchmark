package fr.egaetan.cgbench;

import java.util.Random;

public class GenerateSeeds {

	final static Random random = new Random();
	
	// "shufflePlayer0Seed\u003d956025595\nseed\u003d190157741\ndraftChoicesSeed\u003d830886275\nshufflePlayer1Seed\u003d652093622",
	public static void main(String[] args) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			if (i > 0) {
				res.append(", ");
			}
			res.append("\"");
			res.append("shufflePlayer0Seed=" + generateSeed() + "\n");
			res.append("seed=" + generateSeed() + "\n");
			res.append("draftChoicesSeed=" + generateSeed() + "\n");
			res.append("shufflePlayer1Seed=" + generateSeed() + "\n");
			res.append("\"");
		}
		System.out.println(res);
	}
	
	
	
	public static String generateSeed() {
		return ""+ (random.nextInt(900_000_000)+100_000_000);
	}
}
