package fr.egaetan.cgbench.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.svivien.cgbenchmark.Constants;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Lapi {

	
	
	
	public static void main(String[] args) throws IOException {
		 OkHttpClient client = new OkHttpClient.Builder().readTimeout(600, TimeUnit.SECONDS).build();
		 Retrofit retrofit = new Retrofit.Builder().client(client).baseUrl(Constants.CG_HOST).addConverterFactory(GsonConverterFactory.create()).build();
		 LeaderboardApi api = retrofit.create(LeaderboardApi.class);
		 Call<SuccessLeaderboard> load = api.load(new LeaderboardParam("code4life"));
		 final Response<SuccessLeaderboard> execute = load.execute();
		final SuccessLeaderboard body = execute.body();
		System.out.println(body);
	}
}
