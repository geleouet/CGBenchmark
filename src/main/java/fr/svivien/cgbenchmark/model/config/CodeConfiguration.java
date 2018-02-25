package fr.svivien.cgbenchmark.model.config;

import java.util.List;

public class CodeConfiguration {

	private String sourcePath;
	private Integer nbReplays;
	private String language;
	private List<EnemyConfiguration> enemies;

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setNbReplays(Integer nbReplays) {
		this.nbReplays = nbReplays;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setEnemies(List<EnemyConfiguration> enemies) {
		this.enemies = enemies;
	}

	public List<EnemyConfiguration> getEnemies() {
		return enemies;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public Integer getNbReplays() {
		return nbReplays;
	}

	public String getLanguage() {
		return language;
	}

}
