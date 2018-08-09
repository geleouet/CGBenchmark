package fr.egaetan.cgbench.analyser.locm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.GsonBuilder;

import fr.egaetan.cgbench.api.GameApi;
import fr.egaetan.cgbench.api.LastBattlesAgentApi;
import fr.egaetan.cgbench.api.param.GameParam;
import fr.egaetan.cgbench.api.param.LastBattlesParam;
import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.Game;
import fr.egaetan.cgbench.model.leaderboard.LastBattles;
import fr.egaetan.cgbench.model.leaderboard.SuccessGame;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import fr.egaetan.cgbench.model.leaderboard.User;
import fr.svivien.cgbenchmark.model.config.AccountConfiguration;
import fr.svivien.cgbenchmark.model.request.play.PlayResponse.Frame;
import retrofit2.Call;
import retrofit2.Response;

public class AnalyseReplays {

	public void analyseReplays(User user, LastBattlesAgentApi lastBattlesAgentApi, GameApi loadGameApi, AccountConfiguration accountConfiguration) {
		try {
			int myAgentId = user.getAgentId();
			Call<SuccessLastBattles> callLastBattles = lastBattlesAgentApi.load(new LastBattlesParam(myAgentId, -1));
			Response<SuccessLastBattles> executeLB = callLastBattles.execute();
	
			SuccessLastBattles successLastBattles = executeLB.body();
			LastBattles lastBattles = successLastBattles.getSuccess();
			int winP0 = 0;
			int winP1 = 0;
			int lossP0 = 0;
			int lossP1 = 0;
			int total = 0;

			List<Draft> drafts = new ArrayList<>();

			for (Battle b : lastBattles.getLastBattles()) {
				total++;
				Call<SuccessGame> findByGameId = loadGameApi.findByGameId(new GameParam(b.getGameId(), accountConfiguration.getUserId()), accountConfiguration.getAccountCookie());
				Response<SuccessGame> execute = findByGameId.execute();
				Game success = execute.body().getSuccess();

				Draft draft = new Draft();
				draft.picks = picks(success);
	
				if (b.getPlayers().get(0).getPlayerAgentId() == myAgentId) {
					if (success.getRanks()[0] == 0) {
						winP0++;
						draft.winner = 0;
						draft.pos = 0;
					}
					else {
						winP1++;
						draft.winner = 1;
						draft.pos = 1;
					}
				} else {
					if (success.getRanks()[0] == 0) {
						lossP1++;
						draft.winner = 0;
						draft.pos = 1;
					}
					else {
						lossP0++;
						draft.winner = 1;
						draft.pos = 0;
					}
				}

				drafts.add(draft);
			}
			System.out.println(user.getPseudo() + " - P0:" + winP0 + "/" + (winP0 + lossP0) + " " + String.format("%2.2f", (winP0 * 100. / (winP0 + lossP0))) + "% - P1:" + winP1 + "/" + (winP1 + lossP1) + " " + String.format("%2.2f", (winP1 * 100. / (winP1 + lossP1))) + "%");
	
			AllDrafts a = new AllDrafts();
			a.drafts = drafts;

			String json = new GsonBuilder().create().toJson(a);
			File f = new File("./locm");
			if (!f.exists()) {
				f.mkdirs();
			}
			try (FileWriter fw = new FileWriter("./locm/drafts_" + user.getPseudo() + ".json")) {
				fw.write(json);
				fw.flush();
			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public List<Pick> picks(Game game) {
		List<Pick> picks = new ArrayList<>();
		for (Frame f : game.getFrames()) {
			readPick(f).ifPresent(p -> picks.add(p));
		}
		return picks;
	}

	private Optional<Pick> readPick(Frame frame) {
		if (frame.summary != null) {
			if (frame.summary.startsWith("Player $0 chose ")) {
				Pick p = new Pick();
				for (int i = 0; i < 2; i++) {

					String ch0 = frame.summary.split("\n")[i];
					if (ch0.indexOf('(') == -1)
						continue;
					int card0 = -1;
					ch0 = ch0.substring(ch0.indexOf('(') + 1);
					if (ch0.indexOf(')') == -1)
						continue;
					ch0 = ch0.substring(0, ch0.indexOf(')'));
					if (ch0.startsWith("#")) {
						ch0 = ch0.substring(1);
						card0 = Integer.parseInt(ch0);
					}
					if (i == 0) {
						p.p0 = card0;
					} else {
						p.p1 = card0;
					}
				}
				return Optional.of(p);
			}
		}
		return Optional.empty();
	}

}
