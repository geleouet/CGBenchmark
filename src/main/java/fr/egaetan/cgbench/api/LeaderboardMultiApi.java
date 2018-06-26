package fr.egaetan.cgbench.api;

import fr.egaetan.cgbench.api.param.LeaderboardParam;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.svivien.cgbenchmark.Constants;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LeaderboardMultiApi {
	   //@POST("/services/LeaderboardsRemoteService/getFilteredChallengeLeaderboard/")
	    @POST("/services/LeaderboardsRemoteService/getFilteredPuzzleLeaderboard/")
	    @Headers({
	            "Host: www.codingame.com",
	            "Connection: keep-alive",
	            "Content-Length: 64",
	            "Accept: application/json, text/plain, */*",
	            "Origin: " + Constants.CG_HOST,
	            "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.97 Safari/537.36",
	            "Content-Type: application/json;charset=UTF-8",
	            "Accept-Encoding: deflate",
	            "Referer: " + Constants.CG_HOST + "/start",
	            "Accept-Language: fr-FR,fr;q=0.8,en-US;q=0.6,en;q=0.4",
	    })
	    Call<SuccessLeaderboard> load(@Body LeaderboardParam params);

}
