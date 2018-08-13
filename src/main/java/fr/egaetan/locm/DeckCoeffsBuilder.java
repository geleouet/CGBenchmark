package fr.egaetan.locm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

/**
 * @author gaeta
 *
 */
public class DeckCoeffsBuilder {

	//46 154 160, 140 153 160, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1
	//10 154 160, 110 153 160, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1, 1 1 1

	//public static final String JSON = "./locm/carteOrdres_3510.json";
	public static final String JSON = "./locm/carteOrdres_1830.json";
	//	public static final String JSON = "./locm/closetAI/carteOrdres_3570P2.json";
	final static int coefffs[] = {0, 81, 69, 87, 81, 89, 89, 127, 83, 92, 62, 86, 78, 77, 54, 79, 66, 79, 93, 76, 58, 79, 60, 86, 60, 79, 85, 69, 91, 99, 70, 59, 97, 94, 71, 53, 68, 86, 75, 72, 68, 79, 52, 52, 88, 55, 47, 76, 122, 111, 99, 111, 102, 111, 97, 37, 70, 52, 56, 61, 46, 68, 61, 55, 99, 127, 106, 98, 124, 111, 79, 71, 74, 70, 57, 79, 50, 63, 36, 66, 90, 72, 83, 84, 113, 96, 89, 92, 91, 67, 64, 81, 30, 78, 70, 86, 87, 86, 77, 88, 72, 67, 61, 81, 75, 68, 70, 45, 52, 76, 13, 69, 59, 33, 64, 73, 114, 38, 59, 52, 54, 76, 62, 44, 43, 58, 60, 64, 58, 68, 54, 47, 48, 74, 63, 64, 47, 71, 35, 99, 4, 80, 52, 16, 85, 87, 68, 92, 94, 75, 85, 121, 83, -1009, -1009, -1009, -1009, -1009, -1009, -1009, -1009, -9999};
	public static String nextString = "";
	public static boolean hasNext = true;

	//66 69 139, 44 84 158 , 52 54 32,32 157 37, 111 150 50, 50 152 85, 50 82 152, 82 85 152, 116 133 105, 32 52 37, 37 99 82, 99 111 82, 111 157 82, 37 157 82, 82 150 50, 67 80 29, 109 152 9, 9 115 87, 9 145 88, 29 139 158, 33 84 18, 12 12 12, 13 13 13, 14 14 14, 15 15 15, 16 16 16, 17 17 17, 18 18 18, 19 19 19, 30 30 30
	
	//predefinedDraftIds=1 2 3 , 3 2 1 , 2 2 2 ,160 160 160, 150 151 152, 130 131 132, 7 7 7, 8 8 8, 9 9 9, 10 10 10, 11 11 11, 12 12 12, 13 13 13, 14 14 14, 15 15 15, 16 16 16, 17 17 17, 18 18 18, 19 19 19, 20 20 20, 11 11 11, 12 12 12, 13 13 13, 14 14 14, 15 15 15, 16 16 16, 17 17 17, 18 18 18, 19 19 19, 30 30 30
	
	public static void main(String[] args) {
		AllClassOrder read = read();
		read.liste = read.liste.stream().filter(i -> !(i.best == 127 && (i.a == 41 || i.b == 41))).collect(Collectors.toList());
		int idx = 127;

		String collect23 = read.liste.stream().filter(i -> i.best == idx).map(o -> o.toString()).collect(Collectors.joining("\n"));
		System.out.println(collect23);

		System.out.println("--------");

		String collect23b$ = read.liste.stream().filter(i -> i.best == idx)
				.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))//.filter(i -> i.a == idx || i.b == idx)
				.map(o -> o.toString()).collect(Collectors.joining("\n"));
		System.out.println(collect23b$);
		System.out.println("--------");

		
		
		String collect23b = read.liste.stream().filter(i -> i.best == idx)
				.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
				.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
				.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
				//.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
				.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
				.flatMap(i -> Stream.of(i.a, i.b))
				.sorted()
				.distinct()
				.map(o -> o.toString()).collect(Collectors.joining("\n"));
		System.out.println(collect23b);
		
		/*System.out.println("--------");
		System.out.println(IntStream.range(1, 160 + 1).mapToObj(i -> Integer.valueOf(i)).max(Comparator.comparing(idx$ ->
		read.liste.stream().filter(i -> i.best == idx$)
		.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
		.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
		.flatMap(o -> read.liste.stream().filter(i -> i.best == o.a || i.best == o.b))
		.flatMap(i -> Stream.of(i.a, i.b))
		.sorted()
		.distinct().count())));*/

		analyse(read);
	}

	private static AllClassOrder read() {
		try {
			Gson gson = new Gson();
			FileInputStream configFileInputStream = new FileInputStream(JSON);
			JsonReader reader = new JsonReader(new InputStreamReader(configFileInputStream, "UTF-8"));
			return gson.fromJson(reader, AllClassOrder.class);
		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException | UnsupportedEncodingException e) {
		}
		return null;
	}

	public static void analyse(AllClassOrder read) {
		int[] newCoeffs = new int[161];
		int[] newCoeffsb = new int[161];
		for (int i = 0; i <= 160; i++) {
			newCoeffs[i] = -1;
			newCoeffsb[i] = -1;
		}
		
		int current = 170;
		
		while (current > 0) {
			int eq = 0;
			System.out.println("------ " + current +" ---------");
			for (int i = 1; i <= 160; i++) {
				if (newCoeffs[i] < 0) {
					int i$ = i;
					if (read.liste.stream()
							.filter(o -> newCoeffs[o.best] == -1)
							.filter(o -> !(o.best == o.a || o.best == o.b))
							.noneMatch(o -> o.a == i$ || o.b == i$)) {
						System.out.println(i + ":" + coefffs[i]);
						eq++;
						newCoeffsb[i] = current;
						
					}
				}
			}
			for (int i = 0; i <= 160; i++) {
				newCoeffs[i] = newCoeffsb[i];
			}
			current -= eq;
			
			if (eq == 0) {
				List<OrderCarte> collect = read.liste.stream()
				.filter(o -> newCoeffs[o.best] == -1).collect(Collectors.toList());
				System.out.println();
				if (collect.size() == 0) {
					break;
				}
				else {
					System.out.println("Incompatibles");
					hasNext = false;
					break;
				}
			}
		}
		

		StringBuilder sb = prepareNextSeeds(newCoeffs, newCoeffsb);
		
		System.out.println(sb.toString());
		
		StringBuilder v = new StringBuilder();
		v.append("{ 0, ");
		for (int i = 1; i <= 160; i++) {
			if (i > 1) {
				v.append(", ");
			}
			v.append(newCoeffs[i]);
		}
		v.append("};");
		System.out.println(v.toString());

		String collect = IntStream.range(1, 160 + 1).mapToObj(i -> Integer.valueOf(i)).sorted(Comparator.comparing(i -> newCoeffs[i])).map(i -> Integer.toString(i)).collect(Collectors.joining(", ", "", ""));
		System.out.println(collect);
	}

	private static StringBuilder prepareNextSeeds(int[] newCoeffs, int[] newCoeffsb) {
		// Prepare next seed
		StringBuilder sb = new StringBuilder();
		Set<Integer> r = new HashSet<>();
		int n = 0;
		for (int s = 170; s > 0; s --) {
			List<Integer> exaequo = new ArrayList<>();
			for (int i = 0; i <= 160; i++) {
				if (newCoeffs[i] == s) {
					exaequo.add(i);
				}
			}
			
			if (exaequo.size() > 1) {
				// search moins bonne
				int moinsBonne = -1;
				for (int i = 1; i <= 160; i++) {
					if (newCoeffs[i] < s) {
						moinsBonne = i;
					}
				}
				
				if (moinsBonne != -1) {
					for (int z = 0; z < exaequo.size() -1; z++) {
						if (n < 30) {
							
							if (n != 0) {
								sb.append(", ");
							}
							r.add(exaequo.get(z));
							r.add(exaequo.get(z + 1));
							sb.append(exaequo.get(z) + " "  +exaequo.get(z+1) + " " + moinsBonne);
							n++;
						}
						
					}
					
					if (exaequo.size() > 2) {
						for (int z = 0; z < exaequo.size() -2; z++) {
							if (n < 30) {
								
								if (n != 0) {
									sb.append(", ");
								}
								
								sb.append(exaequo.get(z) + " "  +exaequo.get(z+2) + " " + moinsBonne);
								n++;
							}
							
						}
					}
					if (exaequo.size() > 3) {
						for (int z = 0; z < exaequo.size() -3; z++) {
							if (n < 30) {
								
								if (n != 0) {
									sb.append(", ");
								}
								
								sb.append(exaequo.get(z) + " "  +exaequo.get(z+3) + " " + moinsBonne);
								n++;
							}
							
						}
					}
				}
				else {
					// Au hasard
					for (int z = 0; z < exaequo.size() -1; z++) {
						if (n < 30) {
							
							if (n != 0) {
								sb.append(", ");
							}
							
							sb.append(exaequo.get(z) + " "  +exaequo.get(z+1) + " " + exaequo.get(z+1));
							sb.append(", ");
							sb.append(exaequo.get(z) + " "  +exaequo.get(z+1) + " " + exaequo.get(z));
							n++;
							n++;
						}
						
					}
					
				}
				
			}
		}
		
		if (n < 30) {
			for (Integer z : r) {
				int v = newCoeffs[z];
				int q = 0; 
				int qidx = -1;
				for (int i = 1; i <= 160; i++) {
					if (z == i) {
						continue;
					}
					if (newCoeffs[i]< v && newCoeffs[i] > q) {
						q = newCoeffs[i];
						qidx = i;
					}
				}
				
				if (qidx != -1) {
					
					int moinsBonne = -1;
					for (int i = 1; i <= 160; i++) {
						if (newCoeffs[i] < q) {
							moinsBonne = i;
						}
					}
					
					if (moinsBonne != -1) {


						if (n != 0) {
							sb.append(", ");
						}

						sb.append(z + " "  +qidx + " " + moinsBonne);
						newCoeffsb[z] = q;
					}
										
				}
			}
		}
		
		if (n==0) {
			hasNext = false;
		}
		
		for (int t = n; t < 30; t++) {
			if (t != 0) {
				sb.append(", ");
			}
			
			sb.append(1 + " "  +1 + " " + 1);
			
		}
		
		
		nextString = "seed=1\npredefinedDraftIds="+sb.toString();
		
		System.out.println(n);
		return sb;
	}
}
