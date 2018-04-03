package student_player;
import java.util.*;
import java.io.*;

public class MyTools {
    public static HashMap<String, Double> deserializeRAVE() {
    		HashMap<String, Double> moveValue = null;
    		try{
    			FileInputStream fileIn = new FileInputStream("data/RAVE.ser");
    			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
    			moveValue = (HashMap<String, Double>) objectIn.readObject();
    			objectIn.close();
    			fileIn.close();
    			return moveValue;
    		}
    		catch(IOException e){
    	        System.out.println(e);
     		return new HashMap<String, Double>(); //so as to not return null
    	    }
    		catch(ClassNotFoundException e) {
    	        System.out.println("Class not found");
    	        return new HashMap<String, Double>(); //so as to not return null
    	    }
    }
}
