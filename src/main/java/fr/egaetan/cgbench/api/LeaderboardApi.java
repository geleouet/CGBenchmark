package fr.egaetan.cgbench.api;

import fr.egaetan.cgbench.api.param.LeaderboardParam;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import retrofit2.Call;
import retrofit2.http.Body;

public interface LeaderboardApi {
	
	   Call<SuccessLeaderboard> load(@Body LeaderboardParam params);
}
