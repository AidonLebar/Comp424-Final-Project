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
	
	//default values
	public double balance = 0; //adjustment for the asymmetrical game in unseen moves
	public double aggression = 0.3; //how much taking a piece is valued 0.3
	public double grit = 0.3; //penalty against losing pieces
	public double optimism = 0.05; //weighting for kings proximity to corner
	public double familiarity = 0.2; //penalty for making the same move multiple times
	public double commitment = 0.4; //penalty against undoing previous move
	public double liberty = 0.3; //weight on the king being surrounded
	public int foresight = 2; //depth of search
	public int daring = 1995; //timeout limit in ms
	
	String prevMove = ""; //to avoid null pointer on first move

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
    		long start = System.currentTimeMillis();
    		//long testStart = System.currentTimeMillis();
    		//double finalMoveScore;
    		int turnNumber = bs.getTurnNumber();
    		if(turnNumber == 0) { //first turn set up
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
    			
    			start += 28000; //first move gets more time
        		
    		}
 
		//boolean changed = false;
	    TablutMove myMove = (TablutMove)randomMove(bs);
    		if(player == 1) { //player is Swedes
    			double myMoveScore = alphabeta(myMove, bs, foresight, -1000000, 1000000, false, start);
	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
	        for(TablutMove move : moves) {
	        		double moveScore = alphabeta(move, bs, foresight, -1000000, 1000000, false, start);

		       	if(moveScore < -900) { //do not consider moves that will make you lose
		       		continue;
		       	}
		       	if(moveScore > 900) {
		       		myMove = move;
		       		break;
		       	}
	        		if(moveScore > myMoveScore) {
		       		myMove = move;
		        		myMoveScore = moveScore;
		        		//changed = true;
		        	}
	        }
	        //finalMoveScore = myMoveScore;
    		}
    		else { //player is Muscovites
    			double myMoveScore = alphabeta(myMove, bs, foresight, -1000000, 1000000, true, start);
    	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	        for(TablutMove move : moves) {
    	        		double moveScore = alphabeta(move, bs, foresight, -1000000, 1000000, true, start);

    	        		if(moveScore > 900) { //do not consider moves that will make you lose
    	        				continue;
    			    }
    	        		if(moveScore < -900) {
    			       		myMove = move;
    			       		break;
    			       	}
    		       	if(moveScore < myMoveScore) {
    		       		myMove = move;
    		        		myMoveScore = moveScore;
    		        		//changed = true;
    		        	}
    	        }
    	        //finalMoveScore = myMoveScore;
    		}
    		
    		if(moveSeen.containsKey(myMove.toTransportable())){ //makes a move worse each time it is seen
    			moveSeen.put(myMove.toTransportable(), (moveSeen.get(myMove.toTransportable()) + 1));
    		}
    		else {
    			moveSeen.put(myMove.toTransportable(), 1);
    		}
    		
    		prevMove = myMove.toTransportable();
    		
    		//System.out.println("Move: " + myMove.toTransportable() + " Value: " + finalMoveScore);
    		//System.out.println("Turn took: " + (System.currentTimeMillis() - testStart) + " ms");
    		//System.out.println("Changed random move: " + changed);
    	
    		return myMove;
    }
    
    
    public Move randomMove(TablutBoardState bs) { //non-seeded random move
    	 	ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	 	return moves.get(rand.nextInt(moves.size()));
    }
    
    public void setWeights(double w0, double w1, double w2, double w3, double w4, double w5, int d) { //to change params
    		balance = w0;
    		aggression = w1;
    		optimism = w2;
    		familiarity = w3;
    		grit = w4;
    		commitment = w5;
    		foresight = d;
    		
    }
    
    public double[] getWeights() {
    		double[] weights = new double[6];
    		weights[0] = balance;
    		weights[1] = aggression;
    		weights[2] = optimism;
    		weights[3] = familiarity;
    		weights[4] = grit;
    		weights[5] = commitment;
    		return weights;
    }
    
    //RAVE-based evaluation
    //bonuses for winning and taking pieces
    //penalties for undoing or repeating moves and losing pieces
    //hopefully player agnostic for use by alpha beta
    public double eval(TablutMove move, TablutBoardState bs, int prevOpPieces, int currentOpPieces, int prevPieces, int currentPieces, int player) { 
   		if(player == 0) { //muscovites less obsessed with surrounding king
   			liberty -= 0.2;
		}
    	
    		double value;
    		if(moveValue.containsKey(move.toTransportable())) {
			value =  moveValue.get(move.toTransportable());
		}
		else {
			value =  balance; //if it hasn't been seen, give it a "neutral" score
		}
    		
    		int direction; //to account for which side we are playing
    		if(player == 1) {
    			direction = 1;
    		}
    		else {
    			direction = -1;
    		}	
    		
    		if(bs.gameOver()) { //massive bonus for winning
			int winner = bs.getWinner();
			if(winner == 0) {
				value -= 1000.0;
			}
			else if(winner == 1) {
				value += 1000.0;
			}
		}
    		
    		if(prevOpPieces > currentOpPieces) { //bonus for taking pieces with this move
			value += (prevOpPieces - currentOpPieces)*aggression*direction; //because multiple pieces can be taken in one move
		}
    		
    		if(prevPieces > currentPieces) { //penalty for losing pieces
    			value -= (prevPieces - currentPieces)*grit*direction;
    		}
    		
    		if(moveSeen.containsKey(move.toTransportable())) { //penalty against moves we've already made
    			value -= familiarity*moveSeen.get(move.toTransportable());
    		}
    		
    		Coord kingPos = bs.getKingPosition(); //king's distance to corner
	    	if(kingPos != null) {
	    		int distToCorner = Coordinates.distanceToClosestCorner(kingPos);
	    		value += (1.0/distToCorner)*optimism;
	    	}
	    	
//	    	if(move.toTransportable().equals(prevMove)) { //dont immediately undo a move
//	    		value -= commitment*direction;
//	    	}
	    
	    	//if(player == 1) {
		   // 	Coord end = move.getEndPosition(); //piece does not want to move where it is surrounded
		    	value -= opponentsAdjacent(kingPos ,bs)*liberty*direction;
		//}
		    	
		if(player == 0) { //return liberty to same level
		   	liberty += 0.2;
		}
	    	
    		return value;
    }
    
     public int opponentsAdjacent(Coord pos, TablutBoardState bs) { //how many of the opponent pieces are adjacent to a position
    	 	int i = 0;
    	 	for(Coord adjacent: Coordinates.getNeighbors(pos)) {
    	 		if(bs.isOpponentPieceAt(adjacent)){
    	 			i++;
    	 		}
    	 	}
    	 	return i;
     }
    
    //Soft-fail alpha-beta
    public double alphabeta(TablutMove m, TablutBoardState bs, int toMaxDepth, double alpha, double beta, boolean max, long start) {
    		TablutBoardState bsClone = (TablutBoardState) bs.clone();
		bsClone.processMove((TablutMove) m);
		
		if(toMaxDepth == foresight) {
			if(bsClone.gameOver()) { //if game ends this next move, no need to recurse
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
		}
    	
    		if(toMaxDepth <= 0) { //max depth reached
    			int prevOpPieces = bs.getNumberPlayerPieces(opponent);
    			int currentOpPieces = bsClone.getNumberPlayerPieces(opponent);
    			int prevPieces = bs.getNumberPlayerPieces(player);
    			int currentPieces = bsClone.getNumberPlayerPieces(player);
    	 		return eval(m, bs, prevOpPieces, currentOpPieces, prevPieces, currentPieces, bsClone.getTurnPlayer());
    	 	}
    		//MAX PLAYER
    		if(max) {
    			double v = -1000000.0;
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
	    				v = Math.max(v, alphabeta(move, bsClone, toMaxDepth-1, alpha, beta, false, start));
	    				alpha = Math.max(alpha, v);
	    				if(beta <= alpha){
	    					break;
	    				}if((System.currentTimeMillis() - start) >= daring) { //TIMEOUT IMMINENT
	    					break;
	    				}
	    			}
	    			return v;
    			}
    		}
    		 //MIN PLAYER
    		else {
    			double v = 1000000.0;
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
	    				v = Math.min(v, alphabeta(move, bsClone, toMaxDepth-1, alpha, beta, true, start));
	    				beta = Math.min(beta, v);
	    				if(beta <= alpha){
	    					break;
	    				}
	    				if((System.currentTimeMillis() - start) >= daring) { //TIMEOUT IMMINENT
	    					break;
	    				}
	    			}
	    			return v;
    			} 
    		}
    }
}




