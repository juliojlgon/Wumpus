package wumpusworld;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Interface for creating a Wumpus World solver
 * agent.
 * 
 * @author Johan Hagelbäck
 */
public interface Agent 
{
    /**
     * Asks the agent to execute an action.
     */
    public void doAction();
    
    public ArrayList<HashMap<String, Integer>> getAprendiz(); 
}
