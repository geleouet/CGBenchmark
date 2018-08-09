package fr.egaetan.locm;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


// [0.5367392014667135, 0.2900967440056517, 0.2512057555339236, -0.3987560704504643, 0.9884504528810898, 0.21841049749816613, -0.17847048785583175, 0.5312464774756768, -0.13639902503611195, 0.35012701705941157, -0.1277406192969994, -0.07186401124362608, 0.16767395546961258, -0.27523918117034224, 0.2504135476791211, 0.3871220170144331, -0.28973522211294817, 0.35384081528003886, 2.5529846010219095, 1.089279500841822, 2.0430187814248963]
// [0.5336848759945274, 0.2917229593223801, 0.2505938604515249, -0.4003601967736179, 0.9825160672379869, 0.21653339088171897, -0.17464550696203093, 0.5375347374278475, -0.13906208144556229, 0.35421411232807, -0.1338228447858521, -0.07231736912128953, 0.16587248912489685, -0.2816047765951997, 0.25496715116337476, 0.38777436164739376, -0.2890817250668379, 0.3592678413682391, 2.5618369799136014, 1.0901582899807738, 2.066595847794445]
// [0.5337158057422489, 0.2917982374546455, 0.25077315387456217, -0.4005715914625812, 0.9823211420714542, 0.2199185915980682, -0.178408431184491, 0.536969896708633, -0.13823375849431396, 0.3549428001571905, -0.13456555797850078, -0.07202267171195083, 0.16575476312303472, -0.28498306655977623, 0.2577011425530166, 0.387208666989171, -0.2880914008203543, 0.3584402490446673, 2.5620219246975666, 1.0901996412772463, 2.066217780318047]

// [0.5337682090497267, 0.2916153511433952, 0.2505086997360522, -0.400280484331166, 0.9825991914801013, 0.21485580199510532, -0.17281421374707068, 0.5377107135874151, -0.13944783018413817, 0.3522292975649975, -0.13174036473521258, -0.0725940746512843, 0.16605292166174881, -0.2779354565215717, 0.25182502150424857, 0.3879569328638203, -0.2895449266344313, 0.040184835465259625, 2.2421889044098218, 0.7706966475847459, 1.7472305965826653, 2.051989135572534]

public class AnalyseCards {

	static class Param {
		double damage = 1.;
		double health = 1.;
		double playerHP = 1.;
		double opponentHP = 1.;
		double cardDraw = 1.;
		
		double damagehealth = 1.;
		
		double damage_breakthrough = 1.;
		double health_breakthrough = 1.;
		double damage_charge = 1.;
		double health_charge = 1.;
		double damage_drain = 1.;
		double health_drain = 1.;
		double damage_guard = 1.;
		double health_guard = 1.;
		double damage_lethal = 1.;
		double health_lethal = 1.;
		double damage_ward = 1.;
		double health_ward = 1.;
		
		double creature = 1.;
		double itemRed = 1.;
		double itemGreen = 1.;
		double itemBlue = 1.;
		
		public double[] write() {
			double[] res = new double[21];
			res[0] = damage;
			res[1] = health;
			res[2] = playerHP;
			res[3]=  opponentHP;
			res[4] = cardDraw;
			res[5] = damage_breakthrough;
			res[6] = health_breakthrough;
			res[7] = damage_charge;
			res[8] = health_charge;
			res[9] = damage_drain;
			res[10]= health_drain;
			res[11]= damage_guard;
			res[12]= health_guard;
			res[13]= damage_lethal;
			res[14]= health_lethal;
			res[15]= damage_ward;
			res[16]= health_ward;
			res[17]= creature;
			res[18]= itemRed;
			res[19]= itemGreen;
			res[20]= itemBlue;
			//res[21]= damagehealth;
			return res;
		}
		
		public void read(double[] res) {
			damage = res[0];
			health = res[1];
			playerHP = res[2];
			opponentHP = res[3];
			cardDraw = res[4];
			damage_breakthrough = res[5];
			health_breakthrough = res[6];
			damage_charge = res[7];
			health_charge = res[8];
			damage_drain = res[9];
			health_drain = res[10];
			damage_guard = res[11];
			health_guard = res[12];
			damage_lethal = res[13];
			health_lethal = res[14];
			damage_ward = res[15];
			health_ward = res[16];
			creature = res[17];
			itemRed = res[18];
			itemGreen = res[19];
			itemBlue = res[20];
			//damagehealth = res[21];
		}
		
		public Param cloneParam() {
			Param res = new Param();
			res.read(this.write());
			return res;
		}
		
		public void mutate(double ratio) {
			double[] r = write();
			for (int i = 0; i < r.length; i++) {
				if (Math.random() < ratio * 10)
					r[i] += (Math.random() - 0.5) * ratio;
			}
			read(r);
		}

		public double evaluate(Card card) {
			double score = 0;
			score += health * card.health;
			score += damage * card.damage;
			score += playerHP * card.playerHP;
			score += opponentHP * card.opponentHP;
			score += cardDraw * card.cardDraw;
			
			switch (card.type) {
			case CREATURE:
				score+=creature;
				break;
			case BLUE:
				score+=itemBlue;
				break;
			case GREEN:
				score+=itemGreen;
				break;
			case RED:
				score+=itemRed;
				break;
			}
			
			if (card.breakthrough)
				score += damage_breakthrough * card.damage + health_breakthrough * card.health;
			if (card.charge)
				score += damage_charge * card.damage + health_charge * card.health;
			if (card.drain)
				score += damage_drain * card.damage + health_drain * card.health;
			if (card.guard)
				score += damage_guard * card.damage + health_guard * card.health;
			if (card.lethal)
				score += damage_lethal * card.damage + health_lethal * card.health;
			if (card.ward)
				score += damage_ward * card.damage + health_ward * card.health;
			
			score+= damagehealth * damage * health;
			return score;
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.setIn(new FileInputStream(new File("cartes.txt")));
		//System.setOut(new PrintStream(new FileOutputStream(new File("out.txt"))));

		Scanner in = new Scanner(System.in);
		List<Card> cartes = new ArrayList<>();
		
		while (in.hasNext()) {
			String line = in.nextLine();
			String[] split = line.split(";");
			int cost = Integer.parseInt(split[3].trim());
			int damage = Integer.parseInt(split[4].trim());
			int health = Integer.parseInt(split[5].trim());
			int playerHP = Integer.parseInt(split[7].trim());
			int opponentHP = Integer.parseInt(split[8].trim());
			int cardDraw = Integer.parseInt(split[9].trim());
			
			boolean breakthrough = split[6].contains("B");
			boolean charge = split[6].contains("C");
			boolean drain = split[6].contains("D");
			boolean guard = split[6].contains("G");
			boolean lethal = split[6].contains("L");
			boolean ward = split[6].contains("W");
		
			CardType type = split[2].trim().equals("creature") ? CardType.CREATURE :
				(split[2].trim().equals("itemGreen") ?CardType.GREEN : (
						split[2].trim().equals("itemRed") ? CardType.RED : CardType.BLUE));
			if (type != CardType.CREATURE) {
				continue;
			}

			Card c = new Card(cost, type, damage, health, playerHP, opponentHP, cardDraw, breakthrough, charge, drain, guard, lethal, ward);
			c.name = split[1].trim();
			c.id = Integer.parseInt(split[0].trim());
			
			cartes.add(c);
		}
		
		searchCoeffs(cartes);
		
		//analyse(cartes);


	}



	private static void analyse(List<Card> cartes) {
		Param p = new Param();
		double[] d = new double[] {0.5336848759945274, 0.2917229593223801, 0.2505938604515249, -0.4003601967736179, 0.9825160672379869, 0.21653339088171897, -0.17464550696203093, 0.5375347374278475, -0.13906208144556229, 0.35421411232807, -0.1338228447858521, -0.07231736912128953, 0.16587248912489685, -0.2816047765951997, 0.25496715116337476, 0.38777436164739376, -0.2890817250668379, 0.3592678413682391, 2.5618369799136014, 1.0901582899807738, 2.066595847794445};
		p.read(d);
		
		cartes.sort(Comparator.comparing(c -> (c.cost - p.evaluate(c)) / p.evaluate(c)));
		
		cartes.stream().forEach(c -> System.out.println(c.id + ";"+c.name + ";" + c.cost +";" + p.evaluate(c)));
	}



	private static void searchCoeffs(List<Card> cartes) {
		Param current = new Param();
		double[] d = new double[64];
		current.read(d);
		Param best = new Param();
		double bestScore = Double.MAX_VALUE;
		
		while (true) {
			double score = 0.;
			for (Card c : cartes) {
				double diff = current.evaluate(c) - c.cost;
				score += diff * diff;
			}
			
			if (score < bestScore) {
				bestScore = score;
				best = current;
				System.out.println("Best -> " + bestScore);
				System.out.println(Arrays.stream(best.write()).mapToObj(Double::toString).collect(Collectors.joining(", ", "[", "]")));
			}
			
			current = best.cloneParam();
			current.mutate(bestScore / 10000.);
		}
	}
	
}