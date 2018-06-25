package fr.egaetan.cgbench.api;

import fr.egaetan.cgbench.api.param.LastBattlesParam;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import retrofit2.Call;
import retrofit2.http.Body;

public interface LastBattlesInterface {

	public Call<SuccessLastBattles> load(@Body LastBattlesParam param);
	
}
