package student_player;

import java.util.*;
import java.util.Random;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;
import tablut.TablutPlayer;



/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {
	private Random rand = new Random();
	HashMap<String, Double> moveValue = new HashMap<String, Double>();
	int player;
	int opponent;
	public static final double trim = -0.4; //adjustment for the assymetrical game
	public static final double aggression = 0.1; //how much taking a piece is valued

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
	        double myMoveScore = maxValue(myMove, bs, 2, 1);
	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
	        for(TablutMove move : moves) {
		        	double moveScore = maxValue(move, bs, 2, 1);
		       	if(moveScore > myMoveScore) {
		       		myMove = move;
		        		myMoveScore = moveScore;
		        	}
	        }
    		}
    		else { //player is Muscovites
    			double myMoveScore = minValue(myMove, bs, 2, 1);
    	        ArrayList<TablutMove> moves = bs.getAllLegalMoves();
    	        for(TablutMove move : moves) {
    		        	double moveScore = minValue(move, bs, 2, 1);
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
    
    public double eval(Move move) {
    		if(moveValue.containsKey(move.toTransportable())) {
    			return moveValue.get(move.toTransportable());
    		}
    		else {
    			return trim; //if it hasn't been seen, give it a "neutral" score
    		}
    }
    
    public double maxValue(Move m, TablutBoardState bs, int maxDepth, int depth) { //minimax no pruning
    		TablutBoardState bsClone = (TablutBoardState) bs.clone();
    		bsClone.processMove((TablutMove) m);
    		
    		int prevOpPieces = bs.getNumberPlayerPieces(opponent);
    		int currentOpPieces = bs.getNumberPlayerPieces(opponent);
    		
    		int direction; //to account for which side
    		if(player == 1) {
    			direction = 1;
    		}
    		else {
    			direction = -1;
    		}
    		
    		if(depth == maxDepth) {
    			double moveEval = eval(m);
    			if(bsClone.gameOver()) {
    				int winner = bsClone.getWinner();
    				if(winner == 0) {
    					moveEval -= 100;
    				}
    				else if(winner == 1) {
    					moveEval += 100;
    				}
    			}
    			if(prevOpPieces > currentOpPieces) { //bonus for taking pieces with this move
    				moveEval += (prevOpPieces - currentOpPieces)*aggression*direction; //because multiple pieces can be taken in one move
    			}
    			return moveEval;
    			
    		}
    		else {
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
		int currentOpPieces = bs.getNumberPlayerPieces(opponent);
		
		int direction; //to account for which side player is on in capture bonus
		if(player == 1) {
			direction = 1;
		}
		else {
			direction = -1;
		}
    		
		if(depth == maxDepth) {
			double moveEval = eval(m);
			if(bsClone.gameOver()) {
				int winner = bsClone.getWinner();
				if(winner == 0) {
					moveEval -= 100;
				}
				else if(winner == 1) {
					moveEval += 100;
				}
			}
			if(prevOpPieces > currentOpPieces) { //bonus for taking pieces with this move
				moveEval += (prevOpPieces - currentOpPieces)*aggression*direction; //because multiple pieces can be taken in one move
			}
			return moveEval;
		}
    		else {
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




