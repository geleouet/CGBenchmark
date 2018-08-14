package fr.egaetan.locm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fr.egaetan.cgbench.analyser.locm.AllDrafts;
import fr.egaetan.cgbench.analyser.locm.Draft;
import fr.egaetan.cgbench.analyser.locm.Pick;

public class DeckAn {

	static int[] base = new int[] 
//			{ 0, 81, 69, 87, 81, 89, 89, 127, 83, 92, 62, 86, 78, 77, 54, 79, 66, 79, 93, 76, 58, 79, 60, 86, 60, 79, 85, 69, 91, 99, 70, 59, 97, 94, 71, 53, 68, 86, 75, 72, 68, 79, 52, 52, 88, 55, 47, 76, 122, 111, 99, 111, 102, 111, 97, 37, 70, 52, 56, 61, 46, 68, 61, 55, 99, 127, 106, 98, 124, 111, 79, 71, 74, 70, 57, 79, 50, 63, 36, 66, 90, 72, 83, 84, 113, 96, 89, 92, 91, 67, 64, 81, 30, 78, 70, 86, 87, 86, 77, 88, 72, 67, 61, 81, 75, 68, 70, 45, 52, 76, 13, 69, 59, 33, 64, 73, 114, 38, 59, 52, 54, 76, 62, 44, 43, 58, 60, 64, 58, 68, 54, 47, 48, 74, 63, 64, 47, 71, 35, 99, 4, 80, 52, 16, 85, 87, 68, 92, 94, 75, 85, 121, 83, -1009, -1009, -1009, -1009, -1009, -1009, -1009, -1009, -9999 };
			{ 0, 66, 32, 122, 62, 94, 100, 168, 108, 121, 34, 117, 102, 95, 48, 113, 50, 110, 152, 125, 39, 116, 83, 147, 27, 49, 80, 56, 150, 161, 64, 29, 159, 141, 98, 41, 74, 140, 61, 63, 57, 106, 33, 43, 149, 54, 37, 73, 162, 164, 146, 165, 156, 166, 151, 17, 58, 31, 52, 87, 40, 97, 70, 21, 136, 167, 154, 155, 170, 158, 99, 71, 82, 101, 78, 111, 46, 89, 24, 96, 153, 123, 131, 72, 157, 144, 84, 139, 142, 91, 90, 60, 16, 79, 69, 132, 120, 107, 85, 134, 77, 76, 36, 145, 103, 129, 115, 25, 42, 133, 15, 118, 93, 19, 124, 138, 163, 22, 51, 45, 47, 128, 68, 38, 28, 53, 92, 59, 112, 105, 55, 35, 44, 127, 104, 88, 30, 67, 26, 160, 18, 81, 23, 14, 114, 119, 65, 143, 137, 75, 126, 169, 135, 11, 20, 109, 13, 130, 148, 86, 12};
			

	private static final String LOCM_DRAFTS_DIR = "./locm";

	public static void main(String[] args) throws Exception {
		
		List<Card> cards = Card.readCards();
		File f = new File(LOCM_DRAFTS_DIR);

		String[] list = f.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("drafts") && name.endsWith(".json");
			}
		});

		int[] cardsUsed = new int[256];
		int[] cardsUsedTotal = new int[256];
		int[] manaCurve = new int[12 + 1];
		int nbCards = 0;
		int nbDrafts = 0;

		for (String s : list) {
			List<Draft> read = read(s).drafts;
			for (Draft d : read) {

				if (d.winner == 0 && d.pos == 0) {
					nbDrafts++;
					for (Pick p : d.picks) {
						cardsUsed[p.p0]++;
						//cardsUsed[p.p1]--;
						nbCards++;
						manaCurve[cards.get(p.p0 - 1).cost]++;
					}
				} else if (d.winner == 1 && d.pos == 1) {
					/*nbDrafts++;
					for (Pick p : d.picks) {
						//cardsUsed[p.p0]--;
						cardsUsed[p.p1]++;
						nbCards++;
						manaCurve[cards.get(p.p1 - 1).cost]++;
					}*/
				}
			}
		}

		System.out.println("nbs : " + nbDrafts);
		StringBuilder sb = new StringBuilder();
		sb.append("{0");
		for (int i = 1; i < 161; i++) {
			System.out.println(i + "=" + cardsUsed[i] + "(" + base[i] + ")  " + (cardsUsed[i] / (nbDrafts + 0.1) * 255));
			sb.append(", " + cardsUsed[i]);
		}
		sb.append("};");
		for (int i = 0; i <= 12; i++) {
			System.out.println("Mana :" + i + "=" + (manaCurve[i] / (0.0001 + nbDrafts)));
			
		}

		System.out.println(sb.toString());

	}

	static AllDrafts read(String filePath) throws Exception {
		Gson gson = new Gson();
		FileInputStream configFileInputStream = new FileInputStream(LOCM_DRAFTS_DIR + "/" + filePath);
		JsonReader reader = new JsonReader(new InputStreamReader(configFileInputStream, "UTF-8"));
		return gson.fromJson(reader, AllDrafts.class);
	}


}
