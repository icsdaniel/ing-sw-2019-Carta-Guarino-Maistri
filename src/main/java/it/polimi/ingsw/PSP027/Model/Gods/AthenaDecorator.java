package it.polimi.ingsw.PSP027.Model.Gods;

import it.polimi.ingsw.PSP027.Controller.Phase;
import it.polimi.ingsw.PSP027.Model.Game.Cell;
import it.polimi.ingsw.PSP027.Model.Game.GodCard;
import it.polimi.ingsw.PSP027.Model.Game.Player;

/**
 * @author danielecarta
 */

public class AthenaDecorator extends GodPowerDecorator {

    /**
     * Constructor : sets the phase the decorator is decorating and a boolean that if it is set as true tells
     * that the decorator acts when it is played by an opponent of the god card's owner
     * @param decoratedPhase phase the decorator is going to decorate
     * @param bActAsOpponentGod true if the god card will act only when it is being played as an opponent god card, otherwise it is false
     */
    public AthenaDecorator(Phase decoratedPhase, boolean bActAsOpponentGod) {

        super(decoratedPhase, bActAsOpponentGod);
    }

    /**
     * Method called by a decorated turn's MovePhase, it sets a list of candidate cells that for Athena are standard when acting for its owner
     * and when acting for the opponents are a list of standard candidate cells for a move, without the ones that would result in the worker moving up one level
     */

    @Override
    public void evalCandidateCells() {

        // call nested phase evalCandidateCells
        super.evalCandidateCells();

        // Athena override only opponent move phase
        if(getDecoratedPhase().IsAMovePhase() && IsActingOnOpponentPlayer()) {

            // Athena when applied remove cells from the current CandidateCells list that has a level > current worker level

            Cell startingCell = this.getWorker().getWorkerPosition();

            for (Cell candidateCell : this.getGameBoard().getNeighbouringCells(startingCell)) {

                if(this.getCandidateCells().indexOf(candidateCell) != -1) {       //for each candidate cell in neighbouringCells already within the list,
                    if (candidateCell.getLevel() > startingCell.getLevel())       // if the lv is higher than current one, remove it
                    {
                        System.out.println("ATHENA: evalCandidateCells removing cell " + candidateCell.getCellIndex());
                        this.getCandidateCells().remove(candidateCell);
                    }
                }
            }
        }
    }

    /**
     * This method performs a standard move action if it's applied to an opponent worker's move,
     * otherwise it performs the standard move and in addition if the conditions are verified it sets the god card as an opponent's
     * god card for the player's opponents
     * @param chosenCell the cell im moving onto
     */
    @Override
    public void performActionOnCell(Cell chosenCell) {

        super.performActionOnCell(chosenCell);

        if(getDecoratedPhase().IsAMovePhase() && !IsActingOnOpponentPlayer()) {
            if (getWorker().getWorkerPrevPosition().getLevel() < getWorker().getWorkerPosition().getLevel()) {
                applyPowerOnOtherPlayers();
            }
        }
    }

    /**
     * Method to call after athena's move is performed if she has moved up and can then apply her power on the opponent players
     */

    public void applyPowerOnOtherPlayers() {

        Player oppPlayer1 = null, oppPlayer2 = null;
        for (int i = 0; i < 25; i++) {

            Cell cell = this.getGameBoard().getCell(i);

            if (cell.isOccupiedByWorker()) {
                Player player = cell.getOccupyingWorker().getWorkerOwner();
                if (player != this.getWorker().getWorkerOwner())
                {
                    if(oppPlayer1 == null)
                        oppPlayer1 = player;
                    else if (player != oppPlayer1) {
                        oppPlayer2 = player;
                        break;
                    }
                }
            }
        }

        if(oppPlayer1 != null)
            oppPlayer1.addOpponentGodCard(new GodCard(GodCard.GodsType.Athena, GodCard.WhereToApply.Move, GodCard.ToWhomIsApplied.Opponent));
        if(oppPlayer2 != null)
            oppPlayer2.addOpponentGodCard(new GodCard(GodCard.GodsType.Athena, GodCard.WhereToApply.Move, GodCard.ToWhomIsApplied.Opponent));
    }

}
