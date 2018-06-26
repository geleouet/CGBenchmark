package fr.egaetan.cgbench.services;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.egaetan.cgbench.api.LeaderboardApi;
import fr.egaetan.cgbench.api.param.LeaderboardParam;
import fr.egaetan.cgbench.model.leaderboard.Leaderboard;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.egaetan.cgbench.model.leaderboard.User;
import retrofit2.Call;
import retrofit2.Response;

public class SearchAgentId {

	private static final Log LOG = LogFactory.getLog(SearchAgentId.class);

	final LeaderboardApi api;

	public SearchAgentId(LeaderboardApi leaderboardApi) {
		super();
		this.api = leaderboardApi;
	}

	public String searchFor(String game, String userName) {
		Call<SuccessLeaderboard> load = api.load(new LeaderboardParam(game));
		try {
			Response<SuccessLeaderboard> response = load.execute();
			if (response.isSuccess()) {
				SuccessLeaderboard body = response.body();
				if (body.getSuccess() != null) {
					Leaderboard leaderboard = body.getSuccess();
					Optional<User> findByName = leaderboard.findByName(userName);
					return findByName.map(u -> "" + u.getAgentId()).orElse("");
				}
			}
		} catch (IOException e) {
			LOG.error("Unable to load leaderboard", e);
		}
		return "";
	}

}
