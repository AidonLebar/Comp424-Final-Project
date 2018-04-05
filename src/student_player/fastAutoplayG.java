package student_player;

import java.util.ArrayList;
import java.util.Random;

import boardgame.Move;
import tablut.*;

public class fastAutoplayG { //same as fastAutoplay but for greedy instead of random
	static public TablutBoardState bs;
	static public TablutPlayer p1;
	static public TablutPlayer p2;
	
	public static void main(String[] args) {
		int n_games = Integer.parseInt(args[0]);
		if(args.length > 1) {
			randomSimulate(n_games);
		}
		else {
			int draws = 0;
			int sWins = 0;
			int rWins = 0;
			int mWins = 0;
			int swWins = 0;
			for(int i = 0; i < n_games; i++) {
				int winner;
				int player;
				if(i%2 == 0) { //alternating sides
					//System.out.println("SvG");
					winner = SvGSim();
					player = 0;
				}
				else {
					//System.out.println("GvS");
					winner = GvSSim();
					player = 1;
				}
				
				if(winner == 1) {
					System.out.println("Win as " + player);
					sWins++;
					if(player == 0) {
						mWins++;
					}
					else if(player == 1) {
						swWins++;
					}
				}
				else if(winner == -1) {
					System.out.println("Lose as " + player);
					rWins++;
				}
				else {
					System.out.println("Draw as " + player);
					draws++;
					continue;
				}
			}
			//System.out.println("Draws: " + draws + " Student Wins: " + sWins + " Random Wins: " + rWins);
			System.out.println("Draws: " + draws + " Student Wins: " + sWins + " Greedy Wins: " + rWins);
			System.out.println("Wins as Swedes: " + swWins);
			System.out.println("Wins as Muscovites: " + mWins);
		}
		
	}
	
	public static void randomSimulate(int n_games) { //for generating randomized game data
		//long start = System.currentTimeMillis();
		
		for(int i = 0; i < n_games; i++) {
			bs = new TablutBoardState();
			p1 = new GreedyTablutPlayer();
			p2 = new GreedyTablutPlayer();
			while(bs.getTurnNumber() <= bs.MAX_TURNS) {
				TablutMove p1move = (TablutMove)p1.chooseMove(bs);
				System.out.println(p1move.toTransportable());
				bs.processMove(p1move);
				if(bs.gameOver()) {
					if(bs.getWinner() == 0) {
						System.out.println("M");
					}
					else if (bs.getWinner() == 1) {
						System.out.println("S");
					}
					else {
						System.out.println("D");
					}
					break;
				}
				TablutMove p2move = (TablutMove)p2.chooseMove(bs);
				System.out.println(p2move.toTransportable());
				bs.processMove(p2move);
				if(bs.gameOver()) {
					if(bs.getWinner() == 0) {
						System.out.println("M");
					}
					else if (bs.getWinner() == 1) {
						System.out.println("S");
					}
					else {
						System.out.println("D");
					}
					break;
				}
			}
		}
		//long end = System.currentTimeMillis();
		//System.out.println(n_games + " games took " + ((double)(end - start)/1000.0) + " seconds");
	}
	
	public static int GvSSim() { //Greedy vs. Student
		bs = new TablutBoardState();
		p1 = new GreedyTablutPlayer();
		p2 = new StudentPlayer();
		
		while(bs.getTurnNumber() <= bs.MAX_TURNS) {
			TablutMove p1move = (TablutMove)p1.chooseMove(bs);
			bs.processMove(p1move);
			if(bs.gameOver()) {
				if(bs.getWinner() == 0) {
					return -1;
				}
				else if (bs.getWinner() == 1) {
					return 1;
				}
				else {
					return 0;
				}
			}
			TablutMove p2move = (TablutMove)p2.chooseMove(bs);
			bs.processMove(p2move);
			if(bs.gameOver()) {
				if(bs.getWinner() == 0) {
					return -1;
				}
				else if (bs.getWinner() == 1) {
					return 1;
				}
				else {
					return 0;
				}
			}
		}
		return 0;
	}
	
	public static int SvGSim() { //Student vs. Greedy
		bs = new TablutBoardState();
		p1 = new StudentPlayer();
		p2 = new GreedyTablutPlayer();
		
		while(bs.getTurnNumber() <= bs.MAX_TURNS) {
			TablutMove p1move = (TablutMove)p1.chooseMove(bs);
			bs.processMove(p1move);
			if(bs.gameOver()) {
				if(bs.getWinner() == 0) {
					return 1;
				}
				else if (bs.getWinner() == 1) {
					return -1;
				}
				else {
					return 0;
				}
			}
			TablutMove p2move = (TablutMove)p2.chooseMove(bs);
			bs.processMove(p2move);
			if(bs.gameOver()) {
				if(bs.getWinner() == 0) {
					return 1;
				}
				else if (bs.getWinner() == 1) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}
		return 0;
	}

}
