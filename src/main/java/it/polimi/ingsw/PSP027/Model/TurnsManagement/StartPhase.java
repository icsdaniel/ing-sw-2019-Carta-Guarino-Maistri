package it.polimi.ingsw.PSP027.Model.TurnsManagement;

import it.polimi.ingsw.PSP027.Model.Game.Worker;

import java.util.List;

/**
 * @author danielecarta
 */

public class StartPhase extends ConcretePhase {

    private List<Worker> choosableWorkers;

    /**
     * Constuctor
     * @param choosableWorkers
     */

    public StartPhase(List<Worker> choosableWorkers) {
        this.choosableWorkers = choosableWorkers;
    }

    /**
     * @return the list of choosable workers
     */

    public List<Worker> getChoosableWorkers() {
        return choosableWorkers;
    }

    /**
     * Will be called by controller and will set the worker chosen by the player to be moved in next MovePhase
     * @param chosenWorker
     */
    public void setChosenWorker(Worker chosenWorker){
        if (choosableWorkers.contains(chosenWorker)) {
            this.setChosenWorker(chosenWorker);
            this.setDone(true);
        }
    }

    /**
     * Sets the list of choosable workers given a list of workers
     * @param choosableWorkers
     */
    public void setChoosableWorkers(List<Worker> choosableWorkers) {
        this.choosableWorkers = choosableWorkers;
    }

}
