package fr.egaetan.cgbench.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.egaetan.cgbench.api.LastBattlesApi;
import fr.egaetan.cgbench.api.param.LastBattlesParam;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import fr.egaetan.cgbench.ui.ObservableValue;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import retrofit2.Call;
import retrofit2.Response;

public class LastBattlesService implements LastBattlesLoader {
	
	private static final int DELAY_BETWEEN_REQUESTS = 30;

	private static final Log LOG = LogFactory.getLog(LastBattlesService.class);

	private final ObservableValue<GameConfig> currentGame;
	private final ObservableValue<AccountConfiguration> currentLogin;
	private final LastBattlesApi testBattlesApi;
	
	String gameName = null;
	List<Battle> alreadyLoaded = new ArrayList<>();
	ObservableValue<List<Battle>> observable = new ObservableValue<>();
	
	long lastLoad;
	
	public LastBattlesService(ObservableValue<GameConfig> currentGame, ObservableValue<AccountConfiguration> currentLogin, LastBattlesApi testBattlesApi) {
		super();
		this.currentGame = currentGame;
		this.currentLogin = currentLogin;
		this.testBattlesApi = testBattlesApi;
	}


	@Override
	public ObservableValue<List<Battle>> lastBattles() {
		if (this.currentLogin == null) {
			return observable;
		}
		AccountConfiguration config = this.currentLogin.getValue();
		if (config ==  null ||config.getAccountIde() == null || config.getAccountCookie() == null) {
			return observable;
		}
		if (this.currentGame.getValue() != null) {
			String name = this.currentGame.getValue().getName();
			if (gameName == null || name == gameName) {
				if (alreadyLoaded.isEmpty() || (System.currentTimeMillis() - lastLoad > 1_000 * DELAY_BETWEEN_REQUESTS)) {
					lastLoad = System.currentTimeMillis();
					LastBattlesParam param = new LastBattlesParam(""+config.getAccountIde(), 
							alreadyLoaded.stream().min(Comparator.comparing(b -> b.getGameId())).map(b -> b.getGameId()).orElse(-1));
					Call<SuccessLastBattles> load = this.testBattlesApi.load(param);
					try {
						Response<SuccessLastBattles> execute = load.execute();
						if (execute.isSuccess()) {
							SuccessLastBattles body = execute.body();
							if (body.getSuccess() != null) {
								List<Battle> lastBattles = body.getSuccess().getLastBattles();
								if (lastBattles.size() > 1) {
									lastBattles.removeAll(alreadyLoaded);
									alreadyLoaded.addAll(lastBattles);
									
									observable.setValue(alreadyLoaded);
								}
							}
							
						}
					}
					catch (IOException e) {
						LOG.error("Unable to load last battles", e);
					}
				}
				
			}
			else {
				gameName = name;
				alreadyLoaded.clear();
			}
		}
		return observable;
	}
}