package fr.egaetan.cgbench.model.leaderboard;

import java.util.List;

public class Game {
	long gameId;
	String refereeInput;
	int scores[];
	int ranks[];
	List<User> agents;
	Tooltip[] tooltips;

	public long getGameId() {
		return gameId;
	}

	public void setGameId(long gameId) {
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

	public void readRanks(List<Integer> ranks$) {
		this.ranks = ranks$.stream().mapToInt(Integer::valueOf).toArray();
	}

	public void readScores(List<Integer> scores$) {
		this.scores = scores$.stream().mapToInt(Integer::valueOf).toArray();
		
	}

}