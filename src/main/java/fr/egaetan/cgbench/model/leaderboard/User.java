package fr.egaetan.cgbench.model.leaderboard;

import fr.egaetan.cgbench.model.tests_session.League;

public class User {
	int rank;
	double score;
	String programmingLanguage;
	String pseudo;
	int agentId;
	Codingamer codingamer;
	League league;
	String testSessionHandle;
	int percentage;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getProgrammingLanguage() {
		return programmingLanguage;
	}

	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public int getAgentId() {
		return agentId;
	}

	public void setAgentId(int agentId) {
		this.agentId = agentId;
	}

	public Codingamer getCodingamer() {
		return codingamer;
	}

	public void setCodingamer(Codingamer codingamer) {
		this.codingamer = codingamer;
	}

	public boolean matchName(String name) {
		if (codingamer == null || name == null) {
			return false;
		}
		return name.equals(codingamer.pseudo);
	}

	@Override
	public String toString() {
		return codingamer.toString() + " " + agentId + " #" + rank + " " + programmingLanguage;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public League getLeague() {
		return league;
	}

	public void setLeague(League league) {
		this.league = league;
	}

	public String getTestSessionHandle() {
		return testSessionHandle;
	}

	public void setTestSessionHandle(String testSessionHandle) {
		this.testSessionHandle = testSessionHandle;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
}