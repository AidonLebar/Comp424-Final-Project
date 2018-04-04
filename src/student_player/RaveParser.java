package student_player;

import java.io.*;
import java.util.*;

public class RaveParser {	
	public static void main(String[] args) {
		HashMap<String, Integer> moveCount = new HashMap<String, Integer>();
		HashMap<String, Integer> moveScore = new HashMap<String, Integer>();
		HashMap<String, Double> moveValue = new HashMap<String, Double>();
		try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
		    String line;
		    HashSet<String> moves = new HashSet<String>();
		    while ((line = br.readLine()) != null) {
		    		if(line.matches("^[0-9]+ [0-9]+ [0-9]+ [0-9]+ [0-9]+$")) {
		    			moves.add(line);
		    		}
		    		else if(line.matches("M")){ //Muscovite victory = -1
		    			for(String move : moves) {
		    				if(moveCount.containsKey(move)) {
		    					int mc = moveCount.get(move);
		    					moveCount.put(move, mc+1);
		    					int ms = moveScore.get(move);
		    					moveScore.put(move, ms-1);		    					
		    				}
		    				else {
		    					moveCount.put(move, 1);
		    					moveScore.put(move, -1);
		    				}
		    			}
		    			moves.clear(); //clear moves for next game
		    		}
		    		else if(line.matches("S")){ //Swede victory = 1
		    			for(String move : moves) {
		    				if(moveCount.containsKey(move)) {
		    					int mc = moveCount.get(move);
		    					moveCount.put(move, mc+1);
		    					int ms = moveScore.get(move);
		    					moveScore.put(move, ms+1);		    					
		    				}
		    				else {
		    					moveCount.put(move, 1);
		    					moveScore.put(move, 1);
		    				}
		    			}
		    			moves.clear(); //clear moves for next game
		    		}
		    		else if(line.matches("D")){ //draw = 0
		    			for(String move : moves) {
		    				if(moveCount.containsKey(move)) {
		    					int mc = moveCount.get(move);
		    					moveCount.put(move, mc+1);
		    					//Since draw is a 0, moveScore does not need updating
		    				}
		    				else {
		    					moveCount.put(move, 1);
		    					moveScore.put(move, 0);
		    				}
		    			}
		    			moves.clear(); //clear moves for next game9
		    		}
		    		
		    }

		    HashSet<String> insufficientData = new HashSet<String>();
	    				
		    for(String move: moveCount.keySet()) { //calculate value for every move seen
		    		int mc = moveCount.get(move);
		    		int ms = moveScore.get(move);
		    		
		    		if(mc < 20) { //not seen enough for a reliable value, so we will give it the average score
		    			insufficientData.add(move);
		    		}
		    		else {
		    			moveValue.put(move, (((double) ms)/((double)mc)));
		    		}
		    }
		    
		    double valueSum = 0;
		    for(double v: moveValue.values()) { //for calculating average
		    		valueSum += v;
		    	}
		    	double average = (double)valueSum/((double)(moveScore.keySet().size())); //sum of scores over number of moves
		    	moveValue.put("average", average); //add average to HashMap as a special value
		    
		    	for(String move : insufficientData) { //assign average to rarely seen moves
		    		moveValue.put(move, average);
		    	}
		
		    try{ //serialize RAVE data
	                  FileOutputStream fileOut = new FileOutputStream("data/RAVE.ser");
	                  ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
	                  System.out.println("move value is empty: " + moveValue.isEmpty());
	                  objectOut.writeObject(moveValue);
	                  objectOut.close();
	                  fileOut.close();
	                  System.out.println("Serialized HashMap data is saved in RAVE.ser");
	        }
		    catch(IOException e)
	            {
	                  System.out.println("Error while serializing move value Hashmap");
	        }
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
}