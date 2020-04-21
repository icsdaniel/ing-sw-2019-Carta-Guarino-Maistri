package it.polimi.ingsw.PSP027.Model.Gods;

import it.polimi.ingsw.PSP027.Model.Game.Cell;
import it.polimi.ingsw.PSP027.Controller.Phase;

/**
 * @author Elisa Maistri
 */

public class AtlasDecorator extends GodPowerDecorator {

    public AtlasDecorator(Phase decoratedPhase, boolean bActAsOpponentGod) {

        super(decoratedPhase, bActAsOpponentGod);

    }

    /**
     * Method used by Atlas to set the candidate cells on which its player can build on.
     * It's the standard method of checking if the neighbouring cell is not occupied by another worker
     * and has not already a dome
     */

    @Override
    public void evalCandidateCells() { }

    /**
     * Method used by Atlas to add a dome regardless of the level preexisting on the cell if the player checked yes to
     * using its God Card.
     * @param chosenCell cell chosen by the player on which it wants to build the dome
     */

    @Override
    public void performActionOnCell(Cell chosenCell) {
        chosenCell.addDome();
    }
}
