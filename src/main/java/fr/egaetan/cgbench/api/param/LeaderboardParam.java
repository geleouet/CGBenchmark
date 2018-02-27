package fr.egaetan.cgbench.api.param;

import java.util.ArrayList;

import fr.egaetan.cgbench.model.config.GameConfig;

@SuppressWarnings("serial")
public class LeaderboardParam extends ArrayList<String> {

	public LeaderboardParam(String game) {
		add(game);
		add(null);
		add(null);
		add(null);
	}

	public LeaderboardParam(GameConfig config) {
		if (config.getGlobal_name() != null) {
			add(config.getGlobal_name());
		}
		else {
			add(config.getName());
			
		}
		add(null);
		add(null);
		add(null);
	}
}
