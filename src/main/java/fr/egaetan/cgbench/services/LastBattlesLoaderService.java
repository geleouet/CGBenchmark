package fr.egaetan.cgbench.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.egaetan.cgbench.api.LastBattlesInterface;
import fr.egaetan.cgbench.api.param.LastBattlesParam;
import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import fr.egaetan.cgbench.ui.ObservableValue;
import retrofit2.Call;
import retrofit2.Response;

public abstract class LastBattlesLoaderService implements LastBattlesLoader{

	private static final int DELAY_BETWEEN_REQUESTS = 25;
	private static final Log LOG = LogFactory.getLog(LastBattlesService.class);
	protected final LastBattlesInterface testBattlesApi;
	List<Battle> alreadyLoaded = new ArrayList<>();
	ObservableValue<List<Battle>> observable = new ObservableValue<>();
	protected ObservableValue<Double> progress = new ObservableValue<>();
	long lastLoad;

	public LastBattlesLoaderService(LastBattlesInterface testBattlesApi) {
		super();
		this.testBattlesApi = testBattlesApi;
		progress.setValue(1.);
	}

	@Override
	public ObservableValue<Double> progress() {
		return progress;
	}

	@Override
	public ObservableValue<List<Battle>> lastBattles() {
		return loadLastBattles();
	}

	protected ObservableValue<List<Battle>> loadLastBattles() {
		if (alreadyLoaded.isEmpty() || (System.currentTimeMillis() - lastLoad > 1_000 * DELAY_BETWEEN_REQUESTS)) {
			lastLoad = System.currentTimeMillis();
			call(loadGames(lastGameId()));
		}
		return observable;
	}

	protected Call<SuccessLastBattles> loadGames(int lastGameId) {
		return this.testBattlesApi.load(new LastBattlesParam("", lastGameId));
	}

	protected int lastGameId() {
		int paramLastGameId = alreadyLoaded.stream()
				.filter(b -> b.getPlayers().stream().anyMatch(p -> p.getPosition() != 0))
				.max(Comparator.comparing(b -> b.getGameId())).map(b -> b.getGameId()).orElse(-1);
		return paramLastGameId;
	}

	protected void call(Call<SuccessLastBattles> load) {
		try {
			Response<SuccessLastBattles> execute = load.execute();
			if (execute.isSuccess()) {
				SuccessLastBattles body = execute.body();
				if (body.getSuccess() != null) {
					progress.setValue(body.getSuccess().getProgress());
					List<Battle> lastBattles = body.getSuccess().getLastBattles();
					if (lastBattles.size() > 1) {
						alreadyLoaded.removeAll(alreadyLoaded.stream()
								.filter(b -> b.getPlayers().stream().noneMatch(p -> p.getPosition() != 0)).collect(Collectors.toList()));
						lastBattles.removeAll(alreadyLoaded);
						System.out.println("Loaded " + lastBattles.size() + comment());
						alreadyLoaded.addAll(lastBattles);
						
						observable.setValue(alreadyLoaded);
						observable.fire();
					}
				}
				
			}
		}
		catch (IOException e) {
			LOG.error("Unable to load last battles", e);
		}
	}

	protected String comment() {
		return "";
	}

}