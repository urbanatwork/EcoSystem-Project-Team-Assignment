
//New Fire Class – Revised 12/2 - 5:15p 
 
import java.util.List; 
import java.util.Iterator; 
import java.util.Random; 
  
/** 
* A simple model of a deer. 
* Deer age, move, eat grass, and die. 
*  
* @author David J. Barnes and Michael Kölling 
* @version 2016.02.29 
*/ 
public class Fire 
{ 
    // Characteristics shared by all deer (class variables). 
     
    // The age at which a fire can start to breed. 
    private static final int BREEDING_AGE = 2; 
    // The age to which a fire can live. 
    private static final int MAX_AGE = 4; 
    // The likelihood of a fire breeding. 
    private static final double BREEDING_PROBABILITY = 1; 
    // The likelihood of death by fire. 
    private static final double FIRE_DEATH_PROBABILITY = 0; 
    // The maximum number of births. 
    private static final int MAX_LITTER_SIZE = 1; 
    // The food value of a single grass. In effect, this is the 
    // number of steps a fire can go before it has to eat again. 
    private static final int GRASS_FOOD_VALUE = 9; 
    // A shared random number generator to control breeding. 
    private static final Random rand = Randomizer.getRandom(); 
     
    // Individual characteristics (instance fields). 
  
    // The fire's age. 
    private int age; 
    // Whether the fire is alive or not. 
    private boolean alive; 
    // The fire's position. 
    private Location location; 
    // The field occupied. 
    private Field field; 
    // The fire's food level, which is increased by eating grass. 
    private int foodLevel; 
    /** 
     * Create fire! fire can be created as a new born or random age 
     *  
     * @param randomAge If true, the fire will have random age and hunger level. 
     * @param field The field currently occupied. 
     * @param location The location within the field. 
     */ 
    public Fire(boolean randomAge, Field field, Location location) 
    { 
        age = 0; 
        alive = true; 
        this.field = field; 
        setLocation(location); 
        if(randomAge) { 
            age = rand.nextInt(MAX_AGE); 
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);     
        } 
        else { 
            // leave age at 0 
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE); 
        } 
     } 
     
     
    /** 
     * This is what the fire does most of the time: it hunts for 
     * grass and trees. In the process, it might spread, die of hunger, 
     * or die of old age. 
     * @param field The field currently occupied. 
     * @param newFire A list to return newly born fire. 
     */ 
    public void spread(List<Fire> newFire) 
    { 
        incrementAge(); 
        incrementHunger(); 
        if(alive) { 
            giveBirth(newFire);             
            // Move towards a source of food if found. 
            Location newLocation = findFood(); 
            if(newLocation == null) {  
                // No food found - try to move to a free location. 
                newLocation = field.freeAdjacentLocation(location); 
            } 
            // See if it was possible to move. 
            if(newLocation != null) { 
                setLocation(newLocation); 
            } 
            else { 
                // Overcrowding. 
                setDead(); 
            } 
        } 
    } 
  
    /** 
     * Check whether the fire is alive or not. 
     * @return True if the fire is still alive. 
     */ 
    public boolean isAlive() 
    { 
        return alive; 
    } 
  
    /** 
     * Return the fire's location. 
     * @return The fire's location. 
     */ 
    public Location getLocation() 
    { 
        return location; 
    } 
     
    /** 
     * Place the fire at the new location in the given field. 
     * @param newLocation The fire's new location. 
     */ 
    private void setLocation(Location newLocation) 
    { 
        if(location != null) { 
            field.clear(location); 
        } 
        location = newLocation; 
        field.place(this, newLocation); 
    } 
     
    /** 
     * Increase the age. This could result in the fire's death. 
     */ 
    private void incrementAge() 
    { 
        age++; 
        if(age > MAX_AGE) { 
            setDead(); 
        } 
    } 
     
    /** 
     * Make this fire hungry. This could result in the fire's death. 
     */ 
    private void incrementHunger() 
    { 
        foodLevel--; 
        if(foodLevel <= 0) { 
            setDead(); 
        } 
    } 
     
    /** 
     * Look for grass or trees adjacent to the current location. 
     * Only the first live grass or tree is consumed. 
     * @return Where food was found, or null if it wasn't. 
     */ 
    private Location findFood() 
    { 
        List<Location> adjacent = field.adjacentLocations(location); 
        Iterator<Location> it = adjacent.iterator(); 
        while(it.hasNext()) { 
            Location where = it.next(); 
            Object organism = field.getObjectAt(where); 
            if(organism instanceof Tree) { 
                Tree tree = (Tree) organism; 
                if(tree.isAlive() && rand.nextDouble() <= FIRE_DEATH_PROBABILITY) {  
                    tree.setDead(); 
                    foodLevel = GRASS_FOOD_VALUE; 
                    return where; 
                } 
            } 
             
            if(organism instanceof Grass) { 
                Grass grass = (Grass) organism; 
                if(grass.isAlive() && rand.nextDouble() <= FIRE_DEATH_PROBABILITY) {  
                    grass.setDead(); 
                    foodLevel = GRASS_FOOD_VALUE; 
                    return where; 
                } 
            }    
             
        } 
        return null; 
    } 
     
            /** 
     * Check whether or not this fire is to give birth at this step. 
     * New births will be made into free adjacent locations. 
     * @param newFire A list to return newly born fire. 
     */ 
    private void giveBirth(List<Fire> newFire) 
    { 
        // New fire is born into adjacent locations. 
        // Get a list of adjacent free locations. 
        List<Location> free = field.getFreeAdjacentLocations(location); 
        int births = breed(); 
        for(int b = 0; b < births && free.size() > 0; b++) { 
            Location loc = free.remove(0); 
            Fire young = new Fire(true, field, loc); 
            newFire.add(young); 
        } 
    } 
         
    /** 
     * Generate a number representing the number of births, 
     * if it can breed. 
     * @return The number of births (may be zero). 
     */ 
    private int breed() 
    { 
        int births = 0; 
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) { 
            births = rand.nextInt(MAX_LITTER_SIZE) + 1; 
        } 
        return births; 
    } 
  
    /** 
     * A fire can breed if it has reached the breeding age. 
     */ 
    private boolean canBreed() 
    { 
        return age >= BREEDING_AGE; 
    } 
  
    /** 
     * Indicate that the fire is no longer alive. 
     * It is removed from the field. 
     */ 
    private void setDead() 
    { 
        alive = false; 
        if(location != null) { 
            field.clear(location); 
            location = null; 
            field = null; 
        } 
    } 
} 
 
 
 
 
