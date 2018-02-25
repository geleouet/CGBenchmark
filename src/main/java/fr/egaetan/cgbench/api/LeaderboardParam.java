package fr.egaetan.cgbench.api;

import java.util.ArrayList;

public class LeaderboardParam extends ArrayList<String> {

	public LeaderboardParam(String game) {
		add(game);
		add(null);
		add(null);
		add(null);
	}
}
