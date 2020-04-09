package it.polimi.ingsw.PSP027.Model.Gods;

import it.polimi.ingsw.PSP027.Model.Game.Cell;
import it.polimi.ingsw.PSP027.Model.Game.Worker;
import it.polimi.ingsw.PSP027.Model.TurnsManagement.ConcretePhase;

public class AtlasDecorator extends GodPowerDecorator{

    public AtlasDecorator(ConcretePhase decoratedPhase) {
        super(decoratedPhase);
    }

    @Override
    public void changeCandidateCells() {

    }

    @Override
    public void updateBoard(Cell chosenCell) {

    }
}
