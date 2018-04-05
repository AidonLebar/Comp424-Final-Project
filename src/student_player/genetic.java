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
		for(int i = 0; i < n_games; i++) {
			StudentPlayer p1 = generate();
			StudentPlayer p2 = generate();
			
			System.out.println("Game " + i);
			
			playGame(p1, p2);

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
					System.out.println("P2 winner : " + Arrays.toString(p2.getWeights()));
					System.out.println("P1 loser : " + Arrays.toString(p1.getWeights()));
					return p2; //p2 won
				}
				else {
					System.out.println("P2 draw : " + Arrays.toString(p2.getWeights()));
					System.out.println("P1 draw : " + Arrays.toString(p1.getWeights()));
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
					System.out.println("P2 winner : " + Arrays.toString(p2.getWeights()));
					System.out.println("P1 loser : " + Arrays.toString(p1.getWeights()));
					return p2; //p2 won
				}
				else {
					System.out.println("P2 draw : " + Arrays.toString(p2.getWeights()));
					System.out.println("P1 draw : " + Arrays.toString(p1.getWeights()));
					return null; //draw
				}
			}
		}
		return null; //error
	}
}
