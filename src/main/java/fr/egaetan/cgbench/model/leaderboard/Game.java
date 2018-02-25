package fr.egaetan.cgbench.model.leaderboard;

import java.util.List;

class Game {
	int gameId;
	String refereeInput;
	int scores[];
	int ranks[];
	List<User> agents;
	Tooltip[] tooltips;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getRefereeInput() {
		return refereeInput;
	}

	public void setRefereeInput(String refereeInput) {
		this.refereeInput = refereeInput;
	}

	public int[] getScores() {
		return scores;
	}

	public void setScores(int[] scores) {
		this.scores = scores;
	}

	public int[] getRanks() {
		return ranks;
	}

	public void setRanks(int[] ranks) {
		this.ranks = ranks;
	}

	public List<User> getAgents() {
		return agents;
	}

	public void setAgents(List<User> agents) {
		this.agents = agents;
	}

	public Tooltip[] getTooltips() {
		return tooltips;
	}

	public void setTooltips(Tooltip[] tooltips) {
		this.tooltips = tooltips;
	}

}