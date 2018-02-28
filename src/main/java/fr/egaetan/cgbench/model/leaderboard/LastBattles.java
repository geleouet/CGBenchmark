package fr.egaetan.cgbench.model.leaderboard;

import java.util.List;

public class LastBattles {
	List<Battle> lastBattles;
	double progress;
	
	public List<Battle> getLastBattles() {
		return lastBattles;
	}

	public void setLastBattles(List<Battle> lastBattles) {
		this.lastBattles = lastBattles;
	}

	public double getProgress() {
		return progress;
	}

	public void setProgress(double progress) {
		this.progress = progress;
	}

}