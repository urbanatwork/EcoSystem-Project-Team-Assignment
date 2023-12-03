import java.util.Random;  
  
import java.util.List;  
  
import java.util.ArrayList;  
  
import java.util.Iterator;  
  
import java.awt.Color;  
  
   
  
/**  
  
* A simple Ecosystem simulator, based on a rectangular field containing   
  
* deer, trees and grass.  
  
*   
  
* @author David J. Barnes and Michael Kölling  
  
* @version 2016.02.29  
  
*/  
  
public class Simulator  
  
{  
  
    // Constants representing configuration information for the simulation.  
  
    // The default width for the grid.  
  
    private static final int DEFAULT_WIDTH = 12;  
  
    // The default depth of the grid.  
  
    private static final int DEFAULT_DEPTH = 12;  
  
    // The probability that a deer will be created in any given grid position.  
  
    private static final double DEER_CREATION_PROBABILITY = 0.02;  
  
    // The probability that a grass will be created in any given position.  
  
    private static final double GRASS_CREATION_PROBABILITY = 0.08;   
  
    // The probability that a tree will be created in any given position.  
  
    private static final double TREE_CREATION_PROBABILITY = 0.01;  
     
    private static final double FIRE_CREATION_PROBABILITY = 0.009;  
  
   
  
    // Lists of organisms in the field.  
  
    private List<Grass> grass;  
  
    private List<Deer> deer;  
  
    private List<Tree> tree;  
     
    private List<Fire> fire;  
  
    // The current state of the field.  
  
    private Field field;  
  
    // The current step of the simulation.  
  
    private int step;  
  
    // A graphical view of the simulation.  
  
    private SimulatorView view;  
  
      
  
    /**  
  
     * Construct a simulation field with default size.  
  
     */  
  
    public Simulator()  
  
    {  
  
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);  
  
    }  
  
      
  
    /**  
  
     * Create a simulation field with the given size.  
  
     * @param depth Depth of the field. Must be greater than zero.  
  
     * @param width Width of the field. Must be greater than zero.  
  
     */  
  
    public Simulator(int depth, int width)  
  
    {  
  
        if(width <= 0 || depth <= 0) {  
  
            System.out.println("The dimensions must be >= zero.");  
  
            System.out.println("Using default values.");  
  
            depth = DEFAULT_DEPTH;  
  
            width = DEFAULT_WIDTH;  
  
        }  
  
          
  
        grass = new ArrayList<Grass>();  
  
        deer = new ArrayList<Deer>();  
  
        tree = new ArrayList<Tree>();  
         
        fire = new ArrayList<Fire>();  
  
        field = new Field(depth, width);  
  
   
  
        // Create a view of the state of each location in the field.  
  
        view = new SimulatorView(depth, width);
  
        view.setColor(Grass.class, Color.GREEN);  
  
        view.setColor(Deer.class, Color.ORANGE);  
  
        view.setColor(Tree.class, Color.BLACK);  
         
        view.setColor(Fire.class, Color.RED);  
  
          
  
        // Setup a valid starting point.  
  
        reset();  
  
    }  
  
      
  
    /**  
  
     * Run the simulation from its current state for a reasonably long   
  
     * period (4000 steps).  
  
     */  
  
    public void runLongSimulation()  
  
    {  
  
        simulate(4000);  
  
    }  
  
      
  
    /**  
  
     * Run the simulation for the given number of steps.  
  
     * Stop before the given number of steps if it ceases to be viable.  
  
     * @param numSteps The number of steps to run for.  
  
     */  
  
    public void simulate(int numSteps)  
  
    {  
  
        for(int step=1; step <= numSteps && view.isViable(field); step++) {  
  
            simulateOneStep();  
  
            // delay(60);   // uncomment this to run more slowly  
  
        }  
  
    }  
  
      
  
    /**  
  
     * Run the simulation from its current state for a single step. Iterate  
  
     * over the whole field updating the state of each deer, grass and tree.  
  
     */  
  
    public void simulateOneStep()  
  
    {  
  
        step++;  
  
   
  
        // Provide space for newborn grass.  
  
        List<Grass> newGrass = new ArrayList<>();          
  
        // Let all grass act.  
  
        for(Iterator<Grass> it = grass.iterator(); it.hasNext(); ) {  
  
            Grass grass = it.next();  
  
            grass.run(newGrass);  
  
            if(! grass.isAlive()) {  
  
                it.remove();  
  
            }  
  
        }  
  
          
  
        // Provide space for newborn deer.  
  
        List<Deer> newDeer = new ArrayList<>();          
  
        // Let all deer act.  
  
        for(Iterator<Deer> it = deer.iterator(); it.hasNext(); ) {  
  
            Deer deer = it.next();  
  
            deer.hunt(newDeer);  
  
            if(! deer.isAlive()) {  
  
                it.remove();  
  
            }  
  
        }  
  
          
  
        // Provide space for newborn trees.  
  
        List<Tree> newTrees = new ArrayList<>();          
  
        // Let all trees act.  
  
        for(Iterator<Tree> it = tree.iterator(); it.hasNext(); ) {  
  
            Tree tree = it.next();  
  
            tree.grow(newTrees);  
  
            if(! tree.isAlive()) {  
  
                it.remove();  
  
            }  
  
        }  
List<Fire> newFire = new ArrayList<>();          
  
        // Let all trees act.  
  
        for(Iterator<Fire> it = fire.iterator(); it.hasNext(); ) {  
  
            Fire fire = it.next();  
  
            fire.spread(newFire);  
  
            if(! fire.isAlive()) {  
  
                it.remove();  
  
            }  
  
        }  
  
        // Add the newly born deer, trees and grass and newly spread fire to the main lists.  
  
        grass.addAll(newGrass);  
  
        deer.addAll(newDeer);  
  
        tree.addAll(newTrees);  
         
        fire.addAll(newFire);  
  
   
  
        view.showStatus(step, field);  
  
    }  
  
          
  
    /**  
  
     * Reset the simulation to a starting position.  
  
     */  
  
    public void reset()  
  
    {  
  
        step = 0;  
  
        grass.clear();  
  
        deer.clear();  
  
        tree.clear(); 
         
        fire.clear();  
  
        populate();  
  
          
  
        // Show the starting state in the view.  
  
        view.showStatus(step, field);  
  
    }  
  
      
  
    /**  
  
     * Randomly populate the field with deer, trees and grass.  
  
     */  
  
    private void populate()  
  
    {  
  
        Random rand = Randomizer.getRandom();  
  
        field.clear();  
  
        for(int row = 0; row < field.getDepth(); row++) {  
  
            for(int col = 0; col < field.getWidth(); col++) {  
  
                if(rand.nextDouble() <= DEER_CREATION_PROBABILITY) {  
  
                    Location location = new Location(row, col);  
  
                    Deer young = new Deer(true, field, location);  
  
                    deer.add(young);  
  
                }  
  
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {  
  
                    Location location = new Location(row, col);  
  
                    Grass young = new Grass(true, field, location);  
  
                    grass.add(young);  
  
                }  
  
                else if(rand.nextDouble() <= TREE_CREATION_PROBABILITY) {  
  
                    Location location = new Location(row, col);  
  
                    Tree young = new Tree(true, field, location);  
  
                    tree.add(young);  
  
                }  
                else if (rand.nextDouble() <= FIRE_CREATION_PROBABILITY){ 
                    Location location = new Location (row, col);  
                     
                    Fire young = new Fire(true, field, location);  
                     
                    fire.add(young);  
                } 
  
                // else leave the location empty.  
  
            }  
  
        }  
  
    }  
  
      
  
    /**  
  
     * Pause for a given time.  
  
     * @param millisec  The time to pause for, in milliseconds  
  
     */  
  
    private void delay(int millisec)  
  
    {  
  
        try {  
  
            Thread.sleep(millisec);  
  
        }  
  
        catch (InterruptedException ie) {  
  
            // wake up  
  
        }  
  
    }  
  
}  