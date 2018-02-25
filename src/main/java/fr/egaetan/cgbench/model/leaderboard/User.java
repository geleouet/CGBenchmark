package fr.egaetan.cgbench.model.leaderboard;

public class User {
	int rank;
	double score;
	String programmingLanguage;
	int agentId;
	Codingamer codingamer;

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
}