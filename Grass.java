
import java.util.List; 
import java.util.Random; 
  
/** 
* A simple model of a grass. 
* Grass age, move, reproduce, and die by getting 
* eaten or by fire. 
* @author David J. Barnes and Michael Kölling 
* @version 2016.02.29 
*/ 
public class Grass 
{ 
    // Characteristics shared by all grass (class variables). 
  
    // The age at which a grass can start to breed. 
    private static final int BREEDING_AGE = 5; 
    // The age to which a grass can live. 
    private static final int MAX_AGE = 40; 
    // The likelihood of a grass breeding. 
    private static final double BREEDING_PROBABILITY = 0.12; 
    // The maximum number of births. 
    private static final int MAX_LITTER_SIZE = 4; 
    // A shared random number generator to control breeding. 
    private static final Random rand = Randomizer.getRandom(); 
     
    // Individual characteristics (instance fields). 
     
    // The grass age. 
    private int age; 
    // Whether the grass is alive or not. 
    private boolean alive; 
    // The grass position. 
    private Location location; 
    // The field occupied. 
    private Field field; 
  
    /** 
     * Create a new grass item. A grass may be created with age 
     * zero (a new born) or with a random age. 
     *  
     * @param randomAge If true, the grass will have a random age. 
     * @param field The field currently occupied. 
     * @param location The location within the field. 
     */ 
    public Grass(boolean randomAge, Field field, Location location) 
    { 
        age = 0; 
        alive = true; 
        this.field = field; 
        setLocation(location); 
        if(randomAge) { 
            age = rand.nextInt(MAX_AGE); 
        } 
    } 
     
    /** 
     * This is what the grass does most of the time - it reproduces. 
     * @param newGrass A list to return newly born grass. 
     */ 
    public void run(List<Grass> newGrass) 
    { 
        incrementAge(); 
        if(alive) { 
            giveBirth(newGrass);             
            // Try to move into a free location. 
            Location newLocation = field.freeAdjacentLocation(location); 
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
     * Check whether the grass is alive or not. 
     * @return true if the grass is still alive. 
     */ 
    public boolean isAlive() 
    { 
        return alive; 
    } 
     
    /** 
     * Indicate that the grass is no longer alive. 
     * It is removed from the field. 
     */ 
    public void setDead() 
    { 
        alive = false; 
        if(location != null) { 
            field.clear(location); 
            location = null; 
            field = null; 
        } 
    } 
     
    /** 
     * Return the grass location. 
     * @return The grass location. 
     */ 
    public Location getLocation() 
    { 
        return location; 
    } 
     
    /** 
     * Place the grass at the new location in the given field. 
     * @param newLocation The grass items new location. 
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
     * Increase the age. 
     * This could result in the grass dying. 
     */ 
    private void incrementAge() 
    { 
        age++; 
        if(age > MAX_AGE) { 
            setDead(); 
        } 
    } 
     
    /** 
     * Check whether or not this grass is to give birth at this step. 
     * New births will be made into free adjacent locations. 
     * @param newGrass A list to return newly born grass. 
     */ 
    private void giveBirth(List<Grass> newGrass) 
    { 
        // New grass are born into adjacent locations. 
        // Get a list of adjacent free locations. 
        List<Location> free = field.getFreeAdjacentLocations(location); 
        int births = breed(); 
        for(int b = 0; b < births && free.size() > 0; b++) { 
            Location loc = free.remove(0); 
            Grass young = new Grass(false, field, loc); 
            newGrass.add(young); 
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
     * A grass can reproduce if it has reached the breeding age. 
     * @return true if the grass can breed, false otherwise. 
     */ 
    private boolean canBreed() 
    { 
        return age >= BREEDING_AGE; 
    } 
} 