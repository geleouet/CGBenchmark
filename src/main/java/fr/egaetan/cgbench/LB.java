package fr.egaetan.cgbench;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class LB {

	private static final String CG_ROOT_URL = "https://www.codingame.com/services/";

	public static void main(String[] args) throws Exception {
			URL auth;
			auth = new URL(CG_ROOT_URL + "gamesPlayersRankingRemoteService/findLastBattlesAndProgressByAgentId");

			HttpURLConnection conn = connect(auth);
			try (OutputStream outputStream = conn.getOutputStream(); PrintStream printStream = new PrintStream(outputStream);) {
				printStream.print("[" + 1892067 + ", null]");
			}

			String stringFromInputStream = getStringFromInputStream(conn.getInputStream());
			System.out.println(stringFromInputStream);
			/*try (InputStream inputStream = conn.getInputStream()) {
				ObjectMapper mapper = new ObjectMapper();
				ignoreUnknown(mapper);
				SuccessLastBattles lb = mapper.readValue(inputStream, SuccessLastBattles.class);

				return lb.success.lastBattles;
			}*/
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

	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

}
