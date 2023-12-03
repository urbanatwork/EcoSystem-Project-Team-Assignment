import java.awt.Color; 
import java.util.HashMap; 
  
/** 
* This class collects and provides some statistical data on the state  
* of a field. It is flexible: it will create and maintain a counter  
* for any class of object that is found within the field. 
*  
* @author David J. Barnes and Michael Kölling 
* @version 2016.02.29 
*/ 
public class FieldStats 
{ 
    // Counters for each type of entity (deer, grass, tree) in the simulation. 
    private HashMap<Class, Counter> counters; 
    // Whether the counters are currently up to date. 
    private boolean countsValid; 
  
    /** 
     * Construct a FieldStats object. 
     */ 
    public FieldStats() 
    { 
        // Set up a collection for counters for each type of organism that 
        // we might find 
        counters = new HashMap<>(); 
        countsValid = true; 
    } 
  
    /** 
     * Get details of what is in the field. 
     * @return A string describing what is in the field. 
     */ 
    public String getPopulationDetails(Field field) 
    { 
        StringBuffer buffer = new StringBuffer(); 
        if(!countsValid) { 
            generateCounts(field); 
        } 
        for(Class key : counters.keySet()) { 
            Counter info = counters.get(key); 
            buffer.append(info.getName()); 
            buffer.append(": "); 
            buffer.append(info.getCount()); 
            buffer.append(' '); 
        } 
        return buffer.toString(); 
    } 
     
    /** 
     * Invalidate the current set of statistics; reset all  
     * counts to zero. 
     */ 
    public void reset() 
    { 
        countsValid = false; 
        for(Class key : counters.keySet()) { 
            Counter count = counters.get(key); 
            count.reset(); 
        } 
    } 
  
    /** 
     * Increment the count for one class of organism. 
     * @param organismClass The class of organism to increment. 
     */ 
    public void incrementCount(Class organismClass) 
    { 
        Counter count = counters.get(organismClass); 
        if(count == null) { 
            // We do not have a counter for this species yet. 
            // Create one. 
            count = new Counter(organismClass.getName()); 
            counters.put(organismClass, count); 
        } 
        count.increment(); 
    } 
  
    /** 
     * Indicate that an organism count has been completed. 
     */ 
    public void countFinished() 
    { 
        countsValid = true; 
    } 
  
    /** 
     * Determine whether the simulation is still viable. 
     * I.e., should it continue to run. 
     * @return true If there is more than one species alive. 
     */ 
    public boolean isViable(Field field) 
    { 
        // How many counts are non-zero. 
        int nonZero = 0; 
        if(!countsValid) { 
            generateCounts(field); 
        } 
        for(Class key : counters.keySet()) { 
            Counter info = counters.get(key); 
            if(info.getCount() > 0) { 
                nonZero++; 
            } 
        } 
        return nonZero > 1; 
    } 
     
    /** 
     * Generate counts of the number of deer, trees, and rabbits. 
     * These are not kept up to date as deer, trees, and rabbits 
     * are placed in the field, but only when a request 
     * is made for the information. 
     * @param field The field to generate the stats for. 
     */ 
    private void generateCounts(Field field) 
    { 
        reset(); 
        for(int row = 0; row < field.getDepth(); row++) { 
            for(int col = 0; col < field.getWidth(); col++) { 
                Object organism = field.getObjectAt(row, col); 
                if(organism != null) { 
                    incrementCount(organism.getClass()); 
                } 
            } 
        } 
        countsValid = true; 
    } 
} 