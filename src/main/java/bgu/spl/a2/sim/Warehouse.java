package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 * 
 */
public class Warehouse {

    HashMap<String, SuspendingMutex> mutexes;
    HashMap<String,Computer> computers;

    public Warehouse(ArrayList<String> inputComputers){
        this.computers = new HashMap<>();
        this.mutexes = new HashMap<>();

        for (String type:inputComputers) {
            Computer computer = new Computer(type);
            computers.put(type, computer);
            mutexes.put(type, new SuspendingMutex(computer));
        }
    }


    /**
     * @return
     */
    public Promise<Computer> acquireComputer(String computerType){
        return (mutexes.get(computerType)).down();
    }

    public void releaseComputer(String computerType){
        mutexes.get(computerType).up();
    }


    public HashMap<String, SuspendingMutex> getMutexes() {
        return mutexes;
    }

    public SuspendingMutex getSpecificMutex(String computerType){
        return mutexes.get(computerType);
    }

    public HashMap<String, Computer> getComputers() {
        return computers;
    }

    public Computer getSpecificComputer(String computerType){
        return computers.get(computerType);
    }

}
