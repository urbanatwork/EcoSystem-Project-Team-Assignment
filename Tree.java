import java.util.List; 
import java.util.Iterator; 
import java.util.Random; 
  
/** 
* A simple model of a tree. 
* Trees reproduce, remove grass, and die by fire. 
*  
* @author David J. Barnes and Michael Kölling 
* @version 2016.02.29 
*/ 
public class Tree 
{ 
    // Characteristics shared by all trees (class variables). 
     
    // The age at which a tree can start to breed. 
    private static final int BREEDING_AGE = 0; 
    // The age to which a tree can live. 
    private static final int MAX_AGE = 4000; 
    // The likelihood of a tree breeding. 
    private static final double BREEDING_PROBABILITY = 1.0; 
    // The maximum number of births. 
    private static final int MAX_LITTER_SIZE = 1; 
    // The food value of a single grass. 
    private static final int GRASS_FOOD_VALUE = 9; 
    // A shared random number generator to control breeding. 
    private static final Random rand = Randomizer.getRandom(); 
     
    // Individual characteristics (instance fields). 
  
    // The tree's age. 
    private int age; 
    // Whether the tree is alive or not. 
    private boolean alive; 
    // The tree's position. 
    private Location location; 
    // The field occupied. 
    private Field field; 
    // The tree's food level, which is increased by eating rabbits. 
    private int foodLevel; 
  
    /** 
     * Create a tree. A tree can be created as a new born (age zero) 
 *or with a random age. 
     *  
     * @param randomAge If true, the tree will have random age. 
     * @param field The field currently occupied. 
     * @param location The location within the field. 
     */ 
    public Tree(boolean randomAge, Field field, Location location) 
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
     * This is what the tree does most of the time: it does not move, it reproduces. 
     * @param field The field currently occupied. 
     * @param newTrees A list to return newly born trees. 
     */ 
    public void hunt(List<Tree> newTrees) 
    { 
        incrementAge(); 
        incrementHunger(); 
        if(alive) { 
            giveBirth(newTrees);             
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
     * Check whether the tree is alive or not. 
     * @return True if the tree is still alive. 
     */ 
    public boolean isAlive() 
    { 
        return alive; 
    } 
  
    /** 
     * Return the tree's location. 
     * @return The tree's location. 
     */ 
    public Location getLocation() 
    { 
        return location; 
    } 
     
    /** 
     * Place the tree at the new location in the given field. 
     * @param newLocation The tree's new location. 
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
     * Increase the age. This could result in the tree's death. 
     */ 
    private void incrementAge() 
    { 
        age++; 
        if(age > MAX_AGE) { 
            setDead(); 
        } 
    } 
     
    /** 
     * Make this tree more hungry. This could result in the tree's death. 
     */ 
    private void incrementHunger() 
    { 
        foodLevel--; 
        if(foodLevel <= 0) { 
            setDead(); 
        } 
    } 
     
    /** 
     * Look for Grass adjacent to the current location. 
     * Only the first live Grass is eaten. 
     * @return Where food was found, or null if it wasn't. 
     */ 
    private Location findFood() 
    { 
        List<Location> adjacent = field.adjacentLocations(location); 
        Iterator<Location> it = adjacent.iterator(); 
        while(it.hasNext()) { 
            Location where = it.next(); 
            Object organism = field.getObjectAt(where); 
            if(organism instanceof Grass) { 
                Grass grass = (Grass) organism; 
                if(grass.isAlive()) {  
                    grass.setDead(); 
                    foodLevel = GRASS_FOOD_VALUE; 
                    return where; 
                } 
            } 
        } 
        return null; 
    } 
     
    /** 
     * Check whether or not this tree is to give birth at this step. 
     * New births will be made into free adjacent locations. 
     * @param newTrees  A list to return newly born trees. 
     */ 
    private void giveBirth(List<Tree> newTrees) 
    { 
        // New trees are born into adjacent locations. 
        // Get a list of adjacent free locations. 
        List<Location> free = field.getFreeAdjacentLocations(location); 
        int births = breed(); 
        for(int b = 0; b < births && free.size() > 0; b++) { 
            Location loc = free.remove(0); 
            Tree young = new Tree(false, field, loc); 
            newTrees.add(young); 
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
     * A tree can breed if it has reached the breeding age. 
     */ 
    private boolean canBreed() 
    { 
        return age >= BREEDING_AGE; 
    } 
  
    /** 
     * Indicate that the tree is no longer alive. 
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