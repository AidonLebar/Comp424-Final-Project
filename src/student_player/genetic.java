package student_player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import boardgame.Move;
import tablut.*;

public class genetic {
	private static Random rand = new Random();
	
	public static void main(String[] args) {
		int n_games = Integer.parseInt(args[0]);
		int n_matches = Integer.parseInt(args[1]);
		StudentPlayer p1 = generate();
		StudentPlayer p2 = generate();
		
		int win1 = 0;
		int win2 = 0;
		int draw = 0;
		
		for(int m = 0; m < n_matches; m++ ) {
			System.out.println("MATCH " + m);
		
			for(int i = 0; i < n_games; i++) {
				
				System.out.println("Game " + i);
				
				StudentPlayer winner = playGame(p1, p2);
				if(winner == null) {
					draw++;
				}
				else if(winner.equals(p1)){
					win1++;
				}
				else if(winner.equals(p2)) {
					win2++;
				}
				
				//swap sides each game
				StudentPlayer temp = p1;
				p1 = p2;
				p2 = temp;
	
			}
			System.out.println("P1 wins : " + win1 + " P2 wins : " + win2 + " Draws: " + draw);
			
			//1 points for win, 0.5 for draw, 0 for loss
			double p1Score = ((double) win1 + 0.5*draw)/(n_games);
			double p2Score = ((double) win2 + 0.5*draw)/(n_games);
			System.out.println("P1 score: " + p1Score + " P2 score: " + p2Score);
			
			if(p1Score > p2Score) {
				System.out.println("P2 eliminated");
				p2 = generate();
			}
			else if(p2Score > p1Score) {
				System.out.println("P1 eliminated");
				p1 = generate();
			}
			else {
				System.out.println("No elimination playing again");
			}
		}
	}
	
	public static StudentPlayer generate() {
		StudentPlayer player = new StudentPlayer();
		double[] r = randArray();
		player.setWeights(r[0], r[1], r[2], r[3], r[4], r[5], 1);
		return player;
	}
	
	public static double[] randArray(){
		double[] rands = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		for(int i = 0; i < 6; i++) {
			rands[i] = rand.nextDouble();
		}
		return rands;
	}
	
	//Will return null on draw, be wary
	public static StudentPlayer playGame(StudentPlayer p1, StudentPlayer p2) {
		TablutBoardState bs = new TablutBoardState();
		
		while(bs.getTurnNumber() <= bs.MAX_TURNS) {
			Move p1Move = p1.chooseMove(bs);
			
			bs.processMove((TablutMove) p1Move);
			
			if(bs.gameOver()) {
				if(bs.getWinner() == 0) {
					System.out.println("P1 winner : " + Arrays.toString(p1.getWeights()));
					System.out.println("P2 loser : " + Arrays.toString(p2.getWeights()));
					return p1; //p1 won
				}
				else if(bs.getWinner() == 1){
					System.out.println("P1 loser : " + Arrays.toString(p1.getWeights()));
					System.out.println("P2 winner : " + Arrays.toString(p2.getWeights()));
					return p2; //p2 won
				}
				else {
					System.out.println("P1 draw : " + Arrays.toString(p1.getWeights()));
					System.out.println("P2 draw : " + Arrays.toString(p2.getWeights()));
					return null; //draw
				}
			}

			Move p2Move = p2.chooseMove(bs);
			bs.processMove((TablutMove) p2Move);
			
			if(bs.gameOver()) {
				if(bs.getWinner() == 0) {
					System.out.println("P1 winner : " + Arrays.toString(p1.getWeights()));
					System.out.println("P2 loser : " + Arrays.toString(p2.getWeights()));
					return p1; //p1 won
				}
				else if(bs.getWinner() == 1){
					System.out.println("P1 loser : " + Arrays.toString(p1.getWeights()));
					System.out.println("P2 winner : " + Arrays.toString(p2.getWeights()));
					return p2; //p2 won
				}
				else {
					System.out.println("P1 draw : " + Arrays.toString(p1.getWeights()));
					System.out.println("P2 draw : " + Arrays.toString(p2.getWeights()));
					return null; //draw
				}
			}
		}
		return null; //error
	}
}
