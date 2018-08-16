package fr.egaetan.locm;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StaticCoeffsAnalyser {

	
	private static List<Card> cartes;

	private static final int NB_PLAY = 1_000_000;
	
	private static int[] coeffsNeumannNew = { 0, 83, 44, 137, 60, 89, 94, 164, 117, 124, 24, 103, 99, 75, 36, 108, 48, 116, 149, 123, 30, 114, 54, 128, 31, 74, 110, 52, 133, 156, 76, 28, 154, 150, 98, 40, 104, 157, 90, 80, 50, 95, 26, 38, 136, 51, 39, 58, 160, 163, 138, 162, 129, 165, 115, 19, 45, 21, 56, 71, 33, 87, 62, 35, 148, 166, 121, 155, 168, 158, 106, 64, 70, 78, 55, 126, 41, 63, 29, 69, 159, 127, 135, 97, 161, 146, 93, 141, 144, 67, 91, 79, 15, 96, 61, 132, 120, 100, 86, 143, 68, 43, 34, 139, 81, 105, 112, 23, 37, 131, 16, 102, 66, 18, 107, 119, 170, 25, 85, 65, 92, 140, 88, 47, 27, 72, 82, 57, 122, 118, 32, 49, 42, 147, 113, 109, 46, 84, 20, 169, 11, 130, 77, 17, 134, 101, 53, 153, 152, 73, 142, 167, 145, 12, 22, 125, 14, 111, 151, 59, 13};
	private static int[] closetAIDebug = {0, 81, 69, 87, 81, 89, 89, 127, 83, 92, 62, 86, 78, 77, 54, 79, 66, 79, 93, 76, 58, 79, 60, 86, 60, 79, 85, 69, 91, 99, 70, 59, 97, 94, 71, 53, 68, 86, 75, 72, 68, 79, 52, 52, 88, 55, 47, 76, 122, 111, 99, 111, 102, 111, 97, 37, 70, 52, 56, 61, 46, 68, 61, 55, 99, 127, 106, 98, 124, 111, 79, 71, 74, 70, 57, 79, 50, 63, 36, 66, 90, 72, 83, 84, 113, 96, 89, 92, 91, 67, 64, 81, 30, 78, 70, 86, 87, 86, 77, 88, 72, 67, 61, 81, 75, 68, 70, 45, 52, 76, 13, 69, 59, 33, 64, 73, 114, 38, 59, 52, 54, 76, 62, 44, 43, 58, 60, 64, 58, 68, 54, 47, 48, 74, 63, 64, 47, 71, 35, 99, 4, 80, 52, 16, 85, 87, 68, 92, 94, 75, 85, 121, 83, -1009, -1009, -1009, -1009, -1009, -1009, -1009, -1009, -9999};
	private static int[] closetAI1 = { 0, 81, 42, 111, 68, 103, 114, 168, 102, 137, 31, 116, 100, 97, 45, 129, 59, 115, 153, 109, 49, 110, 98, 85, 21, 65, 101, 78, 120, 160, 62, 24, 146, 154, 124, 48, 60, 148, 76, 44, 58, 118, 34, 47, 152, 66, 32, 70, 164, 163, 144, 165, 151, 167, 150, 19, 75, 23, 71, 89, 46, 61, 50, 28, 106, 169, 158, 162, 170, 159, 123, 112, 91, 104, 72, 95, 33, 107, 20, 74, 161, 117, 141, 52, 156, 142, 79, 135, 134, 87, 83, 57, 15, 56, 64, 132, 133, 122, 92, 147, 51, 63, 43, 125, 69, 130, 119, 26, 38, 139, 16, 143, 88, 22, 113, 138, 131, 29, 36, 37, 93, 96, 82, 40, 39, 53, 80, 77, 105, 121, 55, 41, 35, 127, 99, 108, 30, 94, 27, 157, 17, 73, 18, 14, 86, 136, 54, 128, 126, 84, 145, 166, 140, 11, 25, 90, 13, 149, 155, 67, 12};
	private static int[] closetAI2 = { 0, 66, 32, 122, 62, 94, 100, 168, 108, 121, 34, 117, 102, 95, 48, 113, 50, 110, 152, 125, 39, 116, 83, 147, 27, 49, 80, 56, 150, 161, 64, 29, 159, 141, 98, 41, 74, 140, 61, 63, 57, 106, 33, 43, 149, 54, 37, 73, 162, 164, 146, 165, 156, 166, 151, 17, 58, 31, 52, 87, 40, 97, 70, 21, 136, 167, 154, 155, 170, 158, 99, 71, 82, 101, 78, 111, 46, 89, 24, 96, 153, 123, 131, 72, 157, 144, 84, 139, 142, 91, 90, 60, 16, 79, 69, 132, 120, 107, 85, 134, 77, 76, 36, 145, 103, 129, 115, 25, 42, 133, 15, 118, 93, 19, 124, 138, 163, 22, 51, 45, 47, 128, 68, 38, 28, 53, 92, 59, 112, 105, 55, 35, 44, 127, 104, 88, 30, 67, 26, 160, 18, 81, 23, 14, 114, 119, 65, 143, 137, 75, 126, 169, 135, 11, 20, 109, 13, 130, 148, 86, 12};
	private static int[] tm = { 0, 124, 74, 148, 101, 141, 140, 168, 139, 118, 30, 114, 84, 85, 29, 70, 57, 95, 152, 111, 34, 94, 59, 103, 55, 88, 134, 87, 153, 160, 86, 46, 151, 116, 53, 44, 31, 110, 129, 121, 39, 60, 26, 42, 147, 27, 48, 119, 170, 169, 150, 166, 142, 164, 157, 20, 51, 33, 54, 90, 35, 77, 69, 56, 146, 167, 126, 143, 165, 161, 99, 50, 62, 76, 49, 115, 37, 71, 40, 72, 155, 81, 128, 132, 158, 137, 93, 109, 123, 66, 58, 133, 15, 125, 98, 144, 136, 107, 82, 138, 63, 47, 36, 127, 78, 117, 102, 25, 45, 108, 13, 97, 68, 21, 120, 91, 159, 28, 122, 100, 67, 89, 65, 43, 19, 73, 92, 61, 79, 113, 38, 32, 16, 131, 64, 105, 75, 106, 17, 162, 11, 145, 22, 23, 156, 96, 52, 135, 130, 24, 154, 163, 104, 14, 41, 112, 18, 80, 149, 83, 12};
	private static int[] Alpacah = { 0, 100, 53, 142, 90, 125, 130, 167, 122, 133, 41, 128, 102, 85, 47, 115, 50, 105, 154, 119, 45, 123, 59, 145, 35, 88, 121, 58, 141, 150, 72, 33, 151, 146, 80, 36, 65, 132, 95, 93, 54, 101, 31, 38, 149, 61, 39, 99, 166, 165, 158, 164, 157, 162, 152, 20, 56, 23, 46, 67, 25, 98, 70, 37, 147, 168, 153, 156, 170, 163, 113, 75, 82, 89, 62, 111, 43, 84, 21, 77, 155, 103, 137, 112, 160, 138, 94, 136, 143, 60, 63, 104, 16, 97, 81, 139, 140, 117, 91, 135, 74, 57, 40, 144, 96, 109, 106, 22, 32, 129, 17, 107, 79, 18, 110, 116, 161, 26, 69, 49, 86, 114, 71, 48, 28, 44, 64, 51, 76, 92, 30, 27, 29, 124, 73, 83, 42, 78, 24, 159, 13, 108, 34, 15, 127, 126, 52, 134, 148, 66, 131, 169, 120, 12, 19, 87, 14, 68, 118, 55, 11};
	private static int[] rng = new int[160+1];

	public static int precalculatedFaceValueNMahoude[] = { 0, 81, 69, 87, 81, 89, 89, 127, 83, 90, 62, 86, 78, 77, 54, 79, 66, 79, 93, 76, 58, 79, 60, 86, 60, 79, 85, 69, 91, 99, 70, 59, 97, 97, 71, 53, 68, 86, 75, 72, 68, 79, 52, 52, 88, 55, 47, 76, 122, 111, 99, 111, 106, 114, 97, 37, 70, 52, 56, 61, 46, 68, 61, 55, 99, 127, 102, 98, 124, 111, 79, 71, 74, 70, 57, 79, 50, 63, 36, 66, 90, 72, 83, 84, 113, 96, 89, 90, 91, 67, 64, 81, 30, 78, 90, 89, 87, 86, 87, 88, 80, 72, 61, 81, 75, 76, 70, 45, 60, 76, 15, 69, 59, 33, 64, 73, 123, 38, 59, 52, 54, 76, 62, 44, 43, 58, 60, 64, 58, 68, 54, 47, 48, 74, 63, 64, 47, 71, 35, 113, 0, 80, 70, 52, 110, 85, 87, 68, 92, 94, 85, 160, 80, 30, 20, 30, 10, 20, 30, 20, 10 };
	
	private static Random random;

	
	public static void main(String[] args) throws FileNotFoundException {
		cartes = Card.readCards();
		random = new Random();

		int[] coeffs = rng;

		int[] manaCurve = generateManaCurve(coeffs);
		
		
		for (int i = 0; i <= 12; i++) {
//			System.out.println(i + ";" + String.format("%2.2f", (manaCurve[i] / (NB_PLAY +0.00001))));
			System.out.println( String.format("%2.2f", (manaCurve[i] / (NB_PLAY +0.00001))));
			
		}
	}


	private static int[] generateManaCurve(int[] coeffs) {
		int[] manaCurve = new int[12 + 1];
		
		for (int i = 0; i < NB_PLAY; i++) {
			ArrayList<Integer> drafting = generateDraftCards();
			int[] choosen = chooseFromDraft(coeffs, drafting);
			fillManaCurve(choosen, manaCurve);
		}
		return manaCurve;
	}


	private static void fillManaCurve(int[] choosen, int[] manaCurve) {
		for (int i = 0; i< 30; i++) {
	    	manaCurve[cartes.get(choosen[i]-1).cost]++;
	    }
	}


	private static int[] chooseFromDraft(int[] coeffs, ArrayList<Integer> drafting) {
		int[] choosen = new int[30];
		
	    for (int pick = 0; pick < 30; pick++) {
	    	int choosed = -1;
	      
	    	
	      int choice1 = drafting.get(random.nextInt(60));
	      int choice2;
	      do
	      {
	        choice2 = drafting.get(random.nextInt(60));
	      } while (choice2==choice1);
	      int choice3;
	      do
	      {
	        choice3 = drafting.get(random.nextInt(60));
	      } while (choice3==choice1 || choice3==choice2);

	      if (coeffs[choice1] >= coeffs[choice2] && coeffs[choice1] >= coeffs[choice3]) {
	    	  choosed = choice1;
	      }
	      else if (coeffs[choice2] >= coeffs[choice1] && coeffs[choice2] >= coeffs[choice3]) {
	    	  choosed = choice2;
	      }
	      else if (coeffs[choice3] >= coeffs[choice1] && coeffs[choice3] >= coeffs[choice2]) {
	    	  choosed = choice3;
	      }
	      
	      choosen[pick] = choosed;
	      
	    }
		return choosen;
	}


	private static ArrayList<Integer> generateDraftCards() {
		ArrayList<Integer> drafting = new ArrayList<>();
		for (int pick = 0; pick < 60; pick++)
		{
			int i = -1;
			do
			{
				i = 1 + random.nextInt(160);
			} while (drafting.contains(i));
			drafting.add(i);
		}
		return drafting;
	}
	
}