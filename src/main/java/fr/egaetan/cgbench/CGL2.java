package fr.egaetan.cgbench;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.gson.JsonParseException;

import fr.egaetan.cgbench.model.leaderboard.Battle;
import fr.egaetan.cgbench.model.leaderboard.LastBattles;
import fr.egaetan.cgbench.model.leaderboard.Leaderboard;
import fr.egaetan.cgbench.model.leaderboard.Result;
import fr.egaetan.cgbench.model.leaderboard.SuccessGame;
import fr.egaetan.cgbench.model.leaderboard.SuccessLastBattles;
import fr.egaetan.cgbench.model.leaderboard.SuccessLeaderboard;
import fr.egaetan.cgbench.model.leaderboard.Tooltip;
import fr.egaetan.cgbench.model.leaderboard.User;

public class CGL2 {

	final static String CHALLENGE = "getChallengeLeaderboard";
	final static String MULTI = "getPuzzleLeaderboard";
	final static String GAME_NAME = "mean-max";
//	final static String GAME_NAME = "wondev-woman";
//	final static String GAME_NAME = "code4life";
//	final static String GAME_NAME = "coders-of-the-caribbean";

	private static final String CG_ROOT_URL = "https://www.codingame.com/services/";

	public static void main(String[] args) throws Exception {
		
		ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "Watcher");
				t.setDaemon(false);
				return t;
			}
		});
		scheduler.scheduleAtFixedRate(() -> dumpLeaderBoard(), 0, 5, TimeUnit.MINUTES);
	}

	public static void dumpLeaderBoard()  {
		try {
			Leaderboard staff = loadLeaderboard();
			System.out.println("LeaderBoard chargé");
			String le = staff.getUsers().stream().map(s -> s.getRank() +";"+s.getScore()+";"+ s.getCodingamer().getPseudo()).collect(Collectors.joining("\n"));
			System.out.println(le);

			try (FileWriter fw = new FileWriter("c:/temp/leaderboard/leaderBoard_"+new SimpleDateFormat("yyyy_MM_dd__HH_mm").format(new Date())+".txt");) {
				fw.write(le);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void mainTimeouts(String[] args) throws Exception {
		Leaderboard staff = loadLeaderboard();
		System.out.println("LeaderBoard chargé");
		User me = staff.findByName("egaetan").get();

		List<Battle> battles = retrieveLastBattles(me);
		System.out.println("Last Battles chargées");

		List<Battle> lostBattles = battles.stream().filter(b -> b.resultFor(me) == Result.LOSS).collect(Collectors.toList());

		searchTimeout(lostBattles);
		System.out.println("All Games chargés");

		long timeout = lostBattles.stream().filter(b -> b.game.tooltips != null).flatMap(b -> Arrays.stream(b.game.tooltips)).filter(t -> check(t)).count();

		System.out.println(me.agentId + " -> " + "Win : " +
				(battles.stream().filter(b -> b.resultFor(me) == Result.WIN).count()) + "/ " + battles.size() +
				" Loss : " + lostBattles.size() + "/ " + battles.size() + " Timeout(s) : " + timeout + "/ " + battles.size());
	}

	public static boolean check(Tooltip t) {
		return t.text.contains("timeout");
	}

	public static void searchTimeout(List<Battle> battles) throws MalformedURLException, IOException, ProtocolException {
		for (Battle b : battles) {
			URL auth;
			auth = new URL(CG_ROOT_URL + "gameResultRemoteService/findByGameId");

			HttpURLConnection conn = connect(auth);
			try (OutputStream outputStream = conn.getOutputStream(); PrintStream printStream = new PrintStream(outputStream);) {
				printStream.print("[" + b.gameId + ", " + null + "]");
			}

			try (InputStream inputStream = conn.getInputStream()) {
				ObjectMapper mapper = new ObjectMapper();
				ignoreUnknown(mapper);
				SuccessGame game = mapper.readValue(inputStream, SuccessGame.class);
				if (game.getSuccess().getTooltips() != null && game.getSuccess().getTooltips().length > 0) {
					int i = 1;
				}
				b.game = game.success;
			}
		}
	}

	public static void ignoreUnknown(ObjectMapper mapper) {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static List<Battle> retrieveLastBattles(User user) throws MalformedURLException, IOException, ProtocolException {
		URL auth;
		auth = new URL(CG_ROOT_URL + "gamesPlayersRankingRemoteService/findLastBattlesAndProgressByAgentId");

		HttpURLConnection conn = connect(auth);
		try (OutputStream outputStream = conn.getOutputStream(); PrintStream printStream = new PrintStream(outputStream);) {
			printStream.print("[" + user.agentId + ", null]");
		}

		try (InputStream inputStream = conn.getInputStream()) {
			ObjectMapper mapper = new ObjectMapper();
			ignoreUnknown(mapper);
			SuccessLastBattles lb = mapper.readValue(inputStream, SuccessLastBattles.class);

			return lb.success.lastBattles;
		}
	}

	private static HttpURLConnection connect(URL auth) throws IOException, ProtocolException {
		HttpURLConnection conn = (HttpURLConnection) auth.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Access-Control-Allow-Origin", "egaetan.script");
		conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
		conn.connect();
		return conn;
	}

	public static Leaderboard loadLeaderboard() throws MalformedURLException, IOException, ProtocolException, JsonParseException, JsonMappingException {
		URL auth = new URL(CG_ROOT_URL + "LeaderboardsRemoteService/getFilteredChallengeLeaderboard/");

		HttpURLConnection conn = connect(auth);
		try (OutputStream outputStream = conn.getOutputStream(); PrintStream printStream = new PrintStream(outputStream);) {
			printStream.print("[" + GAME_NAME + ", null, null, null]");
		}
		SuccessLeaderboard staff;
		try (InputStream inputStream = conn.getInputStream()) {
			ObjectMapper mapper = new ObjectMapper();
			ignoreUnknown(mapper);
			staff = mapper.readValue(inputStream, SuccessLeaderboard.class);
		}
		return staff.success;
	}

	static String convertStreamToString(java.io.InputStream is) {
		try (@SuppressWarnings("resource")
		Scanner s = new Scanner(is).useDelimiter("\\A");) {
			return s.hasNext() ? s.next() : "";
		}
	}

}