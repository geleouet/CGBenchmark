package fr.egaetan.cgbench.services;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.egaetan.cgbench.Gui;
import fr.egaetan.cgbench.api.LeaderboardApi;
import fr.egaetan.cgbench.api.LeaderboardLeagueApi;
import fr.egaetan.cgbench.api.TestSessionApi;
import fr.egaetan.cgbench.api.param.LeaderboardParam;
import fr.egaetan.cgbench.api.param.LeagueLeaderBoardParam;
import fr.egaetan.cgbench.api.param.TestSessionParam;
import fr.egaetan.cgbench.model.config.GameConfig;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.egaetan.cgbench.model.tests_session.TestSessionResult;
import fr.egaetan.cgbench.ui.ObservableValue;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import retrofit2.Call;
import retrofit2.Response;

public class LeaderBoardLoaderService implements LeaderBoardLoader {

	static final Log LOG = LogFactory.getLog(Gui.class);
	private ObservableValue<GameConfig> currentGame;
	private ObservableValue<AccountConfiguration> currentLogin;
	private LeaderboardLeagueApi leaderboardLeagueApi;
	private LeaderboardApi leaderboardApi;
	private TestSessionApi testSessionApi;
	Integer division = null;
	String gameName = null;

	
	public LeaderBoardLoaderService(ObservableValue<GameConfig> currentGame, ObservableValue<AccountConfiguration> currentLogin, LeaderboardLeagueApi leaderboardLeagueApi, LeaderboardApi leaderboardApi, TestSessionApi testSessionApi) {
		this.currentGame = currentGame;
		this.currentLogin = currentLogin;
		this.leaderboardLeagueApi = leaderboardLeagueApi;
		this.leaderboardApi = leaderboardApi;
		this.testSessionApi = testSessionApi;
	}


	@Override
	public SuccessLeaderboard load() {
		if (this.currentLogin == null) {
			return null;
		}
		AccountConfiguration config = this.currentLogin.getValue();
		if (config.getAccountIde() == null || config.getAccountCookie() == null) {
			return null;
		}
		SuccessLeaderboard res = null;
		try {
			if (this.currentGame.getValue() != null) {
				String name = this.currentGame.getValue().getName();
				if (name == gameName && division != null) {
					res = this.leaderboardLeagueApi.load(new LeagueLeaderBoardParam(division)).execute().body();
				}
				gameName = name;
				Call<TestSessionResult> load = this.testSessionApi.load(new TestSessionParam(config.getAccountIde()), config.getAccountCookie());
				Response<TestSessionResult> execute = load.execute();
				TestSessionResult body = execute.body();
				if (body.getSuccess() != null && body.getSuccess().getCurrentQuestion() != null && body.getSuccess().getCurrentQuestion().getArena() != null && body.getSuccess().getCurrentQuestion().getArena().getDivision() != null) {
					division = body.getSuccess().getCurrentQuestion().getArena().getDivision().getDivisionId();
					res = this.leaderboardLeagueApi.load(new LeagueLeaderBoardParam(division)).execute().body();
				}
				else {
					// contest finished
					division = null;
					res = this.leaderboardApi.load(new LeaderboardParam(this.currentGame.getValue())).execute().body();
				}
			}
		}
		catch (IOException e) {
			LOG.error("Unable to retrieve division", e);
		}
		if (res != null) {
			config.setUser(res.getSuccess().findByName(config.getAccountName()).get());
		}
		return res;
	}
}