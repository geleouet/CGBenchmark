package fr.egaetan.cgbench.services;

import java.util.List;

import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.ui.ObservableValue;

public interface LastBattlesLoader {
	
	ObservableValue<List<Battle>> lastBattles();

	ObservableValue<Double> progress();
}