package fr.svivien.cgbenchmark.model.test;

import fr.egaetan.cgbench.model.leaderboard.User;
import fr.svivien.cgbenchmark.model.config.EnemyConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Test input data
 */
public class TestInput {

    private int seedNumber;
    private String seed;
    private String code;
    private String lang;
    private String codeName;
    private List<EnemyConfiguration> players;

    public TestInput(int seedNumber, String seed, String code, String lang, List<EnemyConfiguration> players, String codeName) {
        this.seedNumber = seedNumber;
        this.seed = seed;
        this.code = code;
        this.lang = lang;
        this.players = players;
		this.codeName = codeName;
    }

    public int getSeedNumber() {
        return seedNumber;
    }

    public String getSeed() {
        return seed;
    }

    public String getCode() {
        return code;
    }

    public List<EnemyConfiguration> getPlayers() {
        return players;
    }

    public String getLang() {
        return lang;
    }

	public List<User> agents() {
		List<User> agents = new ArrayList<>();
		for (int i = 0; i < players.size(); i++) {
			EnemyConfiguration p = players.get(i);
			User u = new User();
			u.setAgentId(p.getAgentId());
			u.setPseudo(p.getName());
			agents.add(u);
		}
		return agents;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
}
