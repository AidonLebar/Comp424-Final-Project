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
	int player;
	int opponent;
	public static final double balance = 0.5; //adjustment for the asymmetrical game in unseen moves
	public static final double aggression = 0.2; //how much taking a piece is valued
	public static final double optimism = 0.1;//weighting for kings proximity to corner

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
    		if(bs.getTurnNumber() == 0) { //first turn set up
    			moveValue = MyTools.deserializeRAVE(); //deserialize action value HashMap 
    			
    			opponent = bs.getOpponent();
    			if(opponent == 0) { //find out which side student player is in this game
        			player = 1;
        		}
        		else {
        			player = 0;
        		}
    		}
    		
	    Move myMove = randomMove(bs);
    		if(player == 1) { //player is Swedes
	        double myMoveScore = maxValue(myMove, bs, 1, 1);
	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
	        for(TablutMove move : moves) {
		        	double moveScore = maxValue(move, bs, 1, 1);
		       	if(moveScore > myMoveScore) {
		       		myMove = move;
		        		myMoveScore = moveScore;
		        	}
	        }
    		}
    		else { //player is Muscovites
    			double myMoveScore = minValue(myMove, bs, 1, 1);
    	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	        for(TablutMove move : moves) {
    		        	double moveScore = minValue(move, bs, 1, 1);
    		       	if(moveScore < myMoveScore) {
    		       		myMove = move;
    		        		myMoveScore = moveScore;
    		        	}
    	        }
    		}
    		return myMove;
    }
    
    
    public Move randomMove(TablutBoardState bs) {
    	 	ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	 	return moves.get(rand.nextInt(moves.size()));
    }
    
    public double eval(Move move, TablutBoardState bs, int prevOpPieces, int currentOpPieces) { //RAVE-based eval with bonuses for winning and taking pieces
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
			else if(winner == 1) {
				value += 100;
			}
		}
    		
    		if(prevOpPieces > currentOpPieces) { //bonus for taking pieces with this move
			value += (prevOpPieces - currentOpPieces)*aggression*direction; //because multiple pieces can be taken in one move
		}  		
    		return value;
    }
    
    public double maxValue(Move m, TablutBoardState bs, int maxDepth, int depth) { //minimax no pruning
    		TablutBoardState bsClone = (TablutBoardState) bs.clone();
    		bsClone.processMove((TablutMove) m);
    		
    		int prevOpPieces = bs.getNumberPlayerPieces(opponent);
    		int currentOpPieces = bsClone.getNumberPlayerPieces(opponent);
    		
    		if(depth >= maxDepth) {
    			double moveEval = eval(m, bsClone, prevOpPieces, currentOpPieces);
    			return moveEval; 			
    		}
    		else {
    			if(bsClone.gameOver()) {
    				int winner = bsClone.getWinner();
    				if(winner == 0) {
    					return -1000.0;
    				}
    				else if(winner == 1) {
    					return 1000.0;
    				}
    			}
    			
    			double max = -1000000; //all moves will be more than this
    			for(TablutMove move: bsClone.getAllLegalMoves()) {
    				double moveVal = minValue(move, bsClone, maxDepth, depth+1);
    				if(moveVal > max) {
    					max = moveVal;
    				}
    			}
    			return max;
    		}
    }
    
    public double minValue(Move m, TablutBoardState bs, int maxDepth, int depth) { //minimax no pruning
		TablutBoardState bsClone = (TablutBoardState) bs.clone();
		bsClone.processMove((TablutMove) m);
		
		int prevOpPieces = bs.getNumberPlayerPieces(opponent);
		int currentOpPieces = bsClone.getNumberPlayerPieces(opponent);
    		
		if(depth >= maxDepth) {
			double moveEval = eval(m, bsClone, prevOpPieces, currentOpPieces);
			return moveEval;
		}
    		else {
    			if(bsClone.gameOver()) {
    				int winner = bsClone.getWinner();
    				if(winner == 0) {
    					return -1000.0;
    				}
    				else if(winner == 1) {
    					return 1000.0;
    				}
    			}
    			
    			double min = 1000000; //all moves will be less than this
    			for(TablutMove move: bsClone.getAllLegalMoves()) {
    				double moveVal = maxValue(move, bsClone, maxDepth, depth+1);
    				if(moveVal < min) {
    					min = moveVal;
    				}
    			}
    			return min;
    		}
    }
}




