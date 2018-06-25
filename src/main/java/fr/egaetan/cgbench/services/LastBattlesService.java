package fr.egaetan.cgbench.services;

import java.util.List;

import fr.egaetan.cgbench.api.LastBattlesApi;
import fr.egaetan.cgbench.api.param.LastBattlesParam;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import fr.egaetan.cgbench.ui.ObservableValue;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import retrofit2.Call;

public class LastBattlesService extends LastBattlesLoaderService {
	
	final ObservableValue<GameConfig> currentGame;
	private final ObservableValue<AccountConfiguration> currentLogin;
	String gameName = null;
	
	public LastBattlesService(ObservableValue<GameConfig> currentGame, ObservableValue<AccountConfiguration> currentLogin, LastBattlesApi testBattlesApi) {
		super(testBattlesApi::load);
		this.currentGame = currentGame;
		this.currentLogin = currentLogin;
	}

	protected boolean notConfigured() {
		return this.currentLogin == null || this.currentLogin.getValue() ==  null ||this.currentLogin.getValue().getAccountIde() == null || this.currentLogin.getValue().getAccountCookie() == null;
	}

	@Override
	protected Call<SuccessLastBattles> loadGames(int paramLastGameId) {
		return this.testBattlesApi.load(new LastBattlesParam(""+this.currentLogin.getValue().getAccountIde(), paramLastGameId));
	}

	
	@Override
	protected ObservableValue<List<Battle>> loadLastBattles() {
		if (notConfigured()) {
			return observable;
		}
		if (this.currentGame.getValue() != null) {
			String name = this.currentGame.getValue().getName();
			if (gameName == null || name == gameName) {
				super.loadLastBattles();
			}
			else {
				gameName = name;
				alreadyLoaded.clear();
			}
		}
		return observable;
	}
}