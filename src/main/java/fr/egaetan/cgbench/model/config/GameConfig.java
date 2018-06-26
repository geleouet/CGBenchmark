package fr.egaetan.cgbench.model.config;

import java.util.List;

public class GameConfig {

	String name;
	String global_name;
	int minOpponents;
	int maxOpponents;
	List<SeedParamConfig> seeds;
	Boolean isChallenge;

	public List<SeedParamConfig> getSeeds() {
		return seeds;
	}

	public void setSeeds(List<SeedParamConfig> seeds) {
		this.seeds = seeds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMinOpponents() {
		return minOpponents;
	}

	public void setMinOpponents(int minOpponents) {
		this.minOpponents = minOpponents;
	}

	public int getMaxOpponents() {
		return maxOpponents;
	}

	public void setMaxOpponents(int maxOpponents) {
		this.maxOpponents = maxOpponents;
	}

	public String getGlobal_name() {
		return global_name;
	}

	public void setGlobal_name(String global_name) {
		this.global_name = global_name;
	}

	public Boolean isChallenge() {
		return isChallenge;
	}

	public void setChallenge(Boolean isChallenge) {
		this.isChallenge = isChallenge;
	}


}
