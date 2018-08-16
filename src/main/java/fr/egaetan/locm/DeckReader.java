package fr.egaetan.locm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.GsonBuilder;

import fr.egaetan.locm.model.AllClassOrder;
import fr.egaetan.locm.model.OrderCarte;
import fr.svivien.cgbenchmark.model.request.play.PlayResponse;
import fr.svivien.cgbenchmark.model.request.play.PlayResponse.Frame;
import fr.svivien.cgbenchmark.model.test.TestInput;

public class DeckReader {

	public static List<OrderCarte> liste = new ArrayList<>();
	
	/*static {
		try {
			Gson gson = new Gson();
			FileInputStream configFileInputStream = new FileInputStream(DeckCoeffsBuilder.JSON);
			JsonReader reader = new JsonReader(new InputStreamReader(configFileInputStream, "UTF-8"));
			final AllClassOrder fromJson = gson.fromJson(reader, AllClassOrder.class);
			liste.addAll(fromJson.liste);
			System.out.println(liste.size() + " decks loaded");
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException e) {
		}
	}*/
	
	
	static int[] values = new int[161];

	static {
		for (int i = 0; i < 161; i++) {
			values[i] = -1;
		}
	}

	static boolean analyse = false;
	
	public static void analyseDeck(TestInput t, PlayResponse r, boolean isMeFirst) {
//		if (!analyse) {
//			return;
//		}
		
		
		if (r == null || r.success == null || r.success.frames == null)
			return;
		List<Frame> frames = r.success.frames;
		if (frames.size() < 61) {
			return;
		}
		if (isMeFirst) {
			analyseSnd(frames);
		}
		else {
			analyseFirst(frames);
		}
		
		
		
		AllClassOrder aco = new AllClassOrder();
		aco.liste = liste;
		
		//liste.clear();
		
		try {
			String json = new GsonBuilder().create().toJson(aco);
			File f = new File("./locm");
			if (!f.exists()) {
				f.mkdirs();
			}
			try (FileWriter fw = new FileWriter("./locm/carteOrdres_" +liste.size()+ ".json")) {
				fw.write(json);
				fw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		DeckCoeffsBuilder.analyse(aco);
		
	}

	private static void analyseSnd(List<Frame> frames) {
		int a = 0;
		int b = 0;
		int c = 0;
		for (int i = 0; i <61; i++) {
			Frame frame = frames.get(i);
			if (frame.agentId == 0 && frame.stderr != null && frame.stderr.length() > 0) {
				String[] split = frame.stderr.split(" ");
				if (split.length == 3) {
					a = Integer.parseInt(split[0]);
					b = Integer.parseInt(split[1]);
					c = Integer.parseInt(split[2]);
				}
			}
			else if (frame.agentId == 1) {
				String trim = frame.stdout.replace("PICK", "").trim();

				int choice = Integer.parseInt(trim.split(" ")[0]);
				/*int v0 = Integer.parseInt(trim.split(" ")[1]);
				int v1 = Integer.parseInt(trim.split(" ")[2]);
				int v2 = Integer.parseInt(trim.split(" ")[3]);

				if (values[a] != -1 && values[a] != v0) {
					System.out.println();
				}
				if (values[b] != -1 && values[b] != v1) {
					System.out.println();
				}
				if (values[c] != -1 && values[c] != v2) {
					System.out.println();
				}

				values[a] = v0;
				values[b] = v1;
				values[c] = v2;

				boolean finished = true;
				for (int ai = 1; ai < 161; ai++) {
					if (values[ai] == -1)
						finished = false;
				}

				if (finished) {
					StringBuilder v = new StringBuilder();
					v.append("{ 0, ");
					for (int ai = 1; ai <= 160; ai++) {
						if (ai > 1) {
							v.append(", ");
						}
						v.append(values[ai]);
					}
					v.append("};");
					System.out.println(v.toString());

					String collect = IntStream.range(1, 160 + 1).mapToObj(i$ -> Integer.valueOf(i$)).sorted(Comparator.comparing(i$ -> values[i$])).map(i$ -> Integer.toString(i$)).collect(Collectors.joining(", ", "", ""));
					System.out.println(collect);

					System.out.println();
				}*/

				OrderCarte o = new OrderCarte();
				o.turn = i/2;
				if (choice == 0) {
					o.best = a;
					o.a = b;
					o.b = c;
				}
				else if (choice == 1) {
					o.best = b;
					o.a = a;
					o.b = c;
				}
				else if (choice == 2) {
					o.best = c;
					o.a = b;
					o.b = a;
				}
				else {
					continue;
				}
				
				liste.add(o);
			}
			else {
				System.out.println();
			}
		}
	}
	private static void analyseFirst(List<Frame> frames) {
		int a = 0;
		int b = 0;
		int c = 0;
		int choice = 0; 
		for (int i = 0; i <61; i++) {
			Frame frame = frames.get(i);
			if (frame.agentId == 1 && frame.stderr != null && frame.stderr.length() > 0) {
				String[] split = frame.stderr.split(" ");
				if (split.length == 3) {
					a = Integer.parseInt(split[0]);
					b = Integer.parseInt(split[1]);
					c = Integer.parseInt(split[2]);
					
					OrderCarte o = new OrderCarte();
					o.turn = i/2;
					if (choice == 0) {
						o.best = a;
						o.a = b;
						o.b = c;
					}
					else if (choice == 1) {
						o.best = b;
						o.a = a;
						o.b = c;
					}
					else if (choice == 2) {
						o.best = c;
						o.a = b;
						o.b = a;
					}
					else {
						continue;
					}
					
					liste.add(o);
				}
			}
			else if (frame.agentId == 0) {
				String trim = frame.stdout.replace("PICK", "").trim();
				choice = Integer.parseInt(trim);
				
			}
			else {
				System.out.println();
			}
		}
	}
}
