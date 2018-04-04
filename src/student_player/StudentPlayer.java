package student_player;

import java.util.*;
import java.util.Random;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;
import coordinates.*;



/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {
	private Random rand = new Random();
	HashMap<String, Double> moveValue = new HashMap<String, Double>();
	HashMap<String, Integer> moveSeen = new HashMap<String, Integer>();
	int player;
	int opponent;
	public static double balance = 0; //adjustment for the asymmetrical game in unseen moves
	public static final double aggression = 0.15; //how much taking a piece is valued
	public static final double optimism = 0.05; //weighting for kings proximity to corner
	public static final double familiarity = 0; //penalty for making the same move multiple times

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260668812");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState bs) {
    		//long start = System.currentTimeMillis();
    		double finalMoveScore;
    		if(bs.getTurnNumber() == 0) { //first turn set up
    			moveValue = MyTools.deserializeRAVE(); //deserialize action value HashMap 
    			
    			opponent = bs.getOpponent();
    			if(opponent == 0) { //find out which side student player is in this game
        			player = 1;
        		}
        		else {
        			player = 0;
        		}
    			
    			if(moveValue.containsKey("average")) { //redundant check, but do it anyways
        			balance = moveValue.get("average");
    			}
    		}
    		
	    Move myMove = randomMove(bs);
    		if(player == 1) { //player is Swedes
    			double myMoveScore = alphabeta(myMove, bs, 1, -1000000, 1000000, true);
	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
	        for(TablutMove move : moves) {
	        		double moveScore = alphabeta(move, bs, 1, -1000000, 1000000, true);
		       	if(moveScore < -90) { //do not consider moves that will make you lose
		       		continue;
		       	}
	        		if(moveScore > myMoveScore) {
		       		myMove = move;
		        		myMoveScore = moveScore;
		        	}
	        }
	        finalMoveScore = myMoveScore;
    		}
    		else { //player is Muscovites
    			double myMoveScore = alphabeta(myMove, bs, 1, -1000000, 1000000, false);
    	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	        for(TablutMove move : moves) {
    	        		double moveScore = alphabeta(move, bs, 1, -1000000, 1000000, false);
    	        		if(moveScore > 90) { //do not consider moves that will make you lose
    			       		continue;
    			       	}
    		       	if(moveScore < myMoveScore) {
    		       		myMove = move;
    		        		myMoveScore = moveScore;
    		        	}
    	        }
    	        finalMoveScore = myMoveScore;
    		}
    		//System.out.println("Move: " + myMove.toTransportable() + " Value: " + finalMoveScore);
    		moveSeen.put(myMove.toTransportable(), 1); //remember the moves we've already made TODO increasing penalty for each time we make it
    		//System.out.println("Turn took: " + (System.currentTimeMillis() - start) + " ms");
    		return myMove;
    }
    
    
    public Move randomMove(TablutBoardState bs) {
    	 	ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	 	return moves.get(rand.nextInt(moves.size()));
    }
    
    //RAVE-based eval with bonuses for winning and taking pieces
    public double eval(Move move, TablutBoardState bs, int prevOpPieces, int currentOpPieces) { 
    		double value;
    		if(moveValue.containsKey(move.toTransportable())) {
			value =  moveValue.get(move.toTransportable());
		}
		else {
			value =  balance; //if it hasn't been seen, give it a "neutral" score
		}
    		
    		int direction; //to account for which side
    		if(player == 1) {
    			direction = 1;
    		}
    		else {
    			direction = -1;
    		}	
    		
    		if(bs.gameOver()) { //massive bonus for winning
			int winner = bs.getWinner();
			if(winner == 0) {
				value -= 100;
			}
			else if(winner == 1) { //a bit redundant
				value += 100;
			}
		}
    		
    		if(prevOpPieces > currentOpPieces) { //bonus for taking pieces with this move
			value += (prevOpPieces - currentOpPieces)*aggression*direction; //because multiple pieces can be taken in one move
		}
    		
    		if(moveSeen.containsKey(move.toTransportable())) { //penalty against moves we've already made
    			value -= direction*familiarity;
    		}
    		
    		Coord kingPos = bs.getKingPosition();
	    	if(kingPos != null) {
	    		int distToCorner = Coordinates.distanceToClosestCorner(kingPos);
	    		value += (8 - distToCorner)*optimism;
	    	}
    		return value;
    }
    
    //Soft-fail alpha-beta
    public double alphabeta(Move m, TablutBoardState bs, int toMaxDepth, double alpha, double beta, boolean max) {
    		TablutBoardState bsClone = (TablutBoardState) bs.clone();
		bsClone.processMove((TablutMove) m);
    	
    		if(toMaxDepth == 0) { //max depth reached
    			int prevOpPieces = bs.getNumberPlayerPieces(opponent);
    			int currentOpPieces = bsClone.getNumberPlayerPieces(opponent);
    	 		return eval(m, bs, prevOpPieces, currentOpPieces);
    	 	}
    		//MAX PLAYER
    		if(max) {
    			double v = -1000000;
    			if(bsClone.gameOver()) { //if game ends this move, no need to recurse
    				if(bsClone.getWinner() == 1) { //swedes win
    					return 1000.0;
    				}
    				else if(bsClone.getWinner() == 0){ //muscovites win
    					return -1000.0;
    				}
    				else {
    					return balance;
    				}
    			}
    			else { //the game is still on
	    			for(TablutMove move: bsClone.getAllLegalMoves()) {
	    				v = Math.max(v, alphabeta(move, bsClone, toMaxDepth-1, alpha, beta, false));
	    				alpha = Math.max(alpha, v);
	    				if(beta <= alpha){
	    					break;
	    				}
	    			}
	    			return v;
    			}
    		}
    		 //MIN PLAYER
    		else {
    			double v = 1000000;
    			if(bsClone.gameOver()) { //if game ends this move, no need to recurse
    				if(bsClone.getWinner() == 1) { //swedes win
    					return 1000.0;
    				}
    				else if(bsClone.getWinner() == 0){ //muscovites win
    					return -1000.0;
    				}
    				else {
    					return balance;
    				}
    			}
    			else { //the game is still on
	    			for(TablutMove move: bsClone.getAllLegalMoves()) {
	    				v = Math.min(v, alphabeta(move, bsClone, toMaxDepth-1, alpha, beta, true));
	    				beta = Math.min(beta, v);
	    				if(beta <= alpha){
	    					break;
	    				}
	    			}
	    			return v;
    			} 
    		}
    }
}




