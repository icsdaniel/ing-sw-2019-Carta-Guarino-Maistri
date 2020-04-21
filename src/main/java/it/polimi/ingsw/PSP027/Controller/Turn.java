package it.polimi.ingsw.PSP027.Controller;

import it.polimi.ingsw.PSP027.Model.Game.Cell;
import it.polimi.ingsw.PSP027.Model.Game.GodCard;
import it.polimi.ingsw.PSP027.Model.Game.Player;
import it.polimi.ingsw.PSP027.Model.Game.Worker;
import it.polimi.ingsw.PSP027.Model.Gods.*;
import it.polimi.ingsw.PSP027.Network.ProtocolTypes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author danielecarta
 *
 */

public class Turn {
    private List<Phase> phaseList;
    private Worker chosenWorker;
    private Player playingPlayer;
    private SantoriniMatch santoriniMatch;
    private boolean bCompleted;
    private boolean applyPower;

    /**
     * Constructor: it is called by santorini match when the setup of the game is done and the turn of the first player can start.
     * It is given the player who is going to play the turn and the santorini match that called it.
     * With the constructor begins question-answer cmmunication with the client that one by one triggers the instantiation of each phase with the data
     * received form the client
     *
     * @param playingPlayer player that plays this turn created by the match
     */

    public Turn(Player playingPlayer, SantoriniMatch santoriniMatch) {
        this.santoriniMatch = santoriniMatch;
        this.playingPlayer = playingPlayer;
        this.phaseList = new ArrayList<>();
        this.bCompleted = false;

        //First communication with the client done by the Turn, asking to choose a worker to play the turn with.
        ChooseWorker();
    }

    /**
     * Method that prepares the command to send to the client when asking it to choose the worker to play the turn with and actually sends the command
     */

    public void ChooseWorker()
    {
        String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_ChooseWorker.toString() + "</id><data>";
        cmd += this.santoriniMatch.boardToXMLString();
        cmd += "</data></cmd>";

        playingPlayer.SendCommand(cmd);
    }

    /* ***************************************************************************************************************
     *                METHODS THAT RECEIVE THE CLIENT RESPONSE AND TRIGGER AN ACTION ON THE TURN                     *
     *                          RESULTING IN ANOTHER COMMUNICATION WITH THE CLIENT                                   *
     * ***************************************************************************************************************/

    /**
     * Method that sets the chosen worker by the user that will play the turn and instantiates the move phase
     *
     * @param chosenWorker worker chosen by the user
     */

    public void setChosenWorker(Worker chosenWorker) {

        this.chosenWorker = chosenWorker;

        if(getPlayingPlayer().getPlayerGod().getToWhomIsApplied() == GodCard.ToWhomIsApplied.Owner)
        {
            // prometheus case
            if(getPlayingPlayer().getPlayerGod().AllowExtraBuildBeforeMove())
            {
                // ask player if wanna build before moving (this will decorate the move subsequent !!!)

                // and leave function.
                return;
            }
        }

        CreateMovePhase();
    }

    /**
     * Method that gets the answer of the client on whether it wants to apply the god's power or not
     * @param answer string containing yes or no
     */
    public void setAnswer(String answer) {

        applyPower = answer.equals("Yes");
    }

    /**
     * Method that updates the board with the new position of the worker
     *
     * @param chosenCellIndex cell where the worker is moving onto
     * @TODO create another move or go on and create a build phase
     */
    public void MoveWorker(int chosenCellIndex) {

        Cell cell = santoriniMatch.getGameBoard().getCell(chosenCellIndex);

        if(phaseList.size()>0)
        {
            phaseList.get(phaseList.size()-1).performActionOnCell(cell);

            if(phaseList.get(phaseList.size()-1).PlayerHasWon())
            {

            }
        }
    }


    /* ******************************************** UTILITY METHODS OF THE TURN ****************************************** */

    /**
     * Method to get the player that is playing the turn
     *
     * @return the playing player
     */

    public Player getPlayingPlayer() {
        return playingPlayer;
    }

    /**
     * Method to get the worker chosen by the user at the instantiation of the turn
     *
     * @return the chosen worker
     */

    public Worker getChosenWorker() {
        return chosenWorker;
    }

    /**
     * Method to get the match who created the turn
     *
     * @return the match
     */

    public SantoriniMatch getSantoriniMatch() {
        return santoriniMatch;
    }

    /**
     * Method to get the phase list of the turn
     *
     * @return the phase list
     */

    public List<Phase> getPhaseList() {
        return phaseList;
    }

    /**
     * Method to set the turn's phase list with a phase list
     *
     * @param phaseList phase list to set as this turn's list
     */

    public void setPhaseList(List<Phase> phaseList) {
        this.phaseList = phaseList;
    }

    /**
     * Method to query in order to tell if the turn is completed
     *
     * @return true if it is completed, otherwise false
     */

    public boolean IsCompleted() {

        return bCompleted;
    }

    /**
     * Method that checks if the player playing this turn has won
     *
     * @return true if it has won, otherwise false
     */

    public boolean CurrentPlayerHasWon() {

        return playingPlayer.HasWon();
    }


    /**
     * Method that prepares the command when asking the client to choose whether to use the god power or not and actually sends the command
     */
    public void askToUSeGodPower() {
        String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_AskBeforeApplyingGod.toString()  + "</id><data>";
        cmd += this.santoriniMatch.boardToXMLString();
        cmd += "</data></cmd>";
        playingPlayer.SendCommand(cmd);
    }

    /**
     * Method that creates a move phase, applying the right decorator to it
     */

    public void CreateMovePhase()
    {
        // create move phase and apply decorator to it.
        // the decorated resulting phase is the one that is stored on the phase list
        MovePhase phase = new MovePhase();
        phase.Init(this.chosenWorker, this.santoriniMatch.getGameBoard());

        // start applying player's own god
        Phase playergodphase = applyDecorator(phase, playingPlayer.getPlayerGod().getGodType(), false);

        // and then apply opponent gods to the already decorated phase by the player's own god card
        if(getPlayingPlayer().getOpponentsGodCards().size() > 0)
        {
            Phase opponentgodphase1 = applyDecorator(playergodphase, getPlayingPlayer().getOpponentsGodCards().get(0).getGodType(), true);
            if(getPlayingPlayer().getOpponentsGodCards().size()>1)
            {
                Phase opponentgodphase2 = applyDecorator(opponentgodphase1, getPlayingPlayer().getOpponentsGodCards().get(1).getGodType(), true);
                phaseList.add(opponentgodphase2);
            }
            else
                phaseList.add(opponentgodphase1);
        }
        else
            phaseList.add(playergodphase);

        phaseList.get(phaseList.size()-1).startPhase();
    }

    /**
     * Method that creates a build phase, applying the right decorator to it
     */

    public void CreateBuildPhase()
    {
        // create build phase and apply decorator to it.
        // the decorated resulting phase is the one that is stored on the phase list
        BuildPhase phase = new BuildPhase();
        phase.Init(this.chosenWorker, this.santoriniMatch.getGameBoard());

        Phase playergodphase = applyDecorator(phase, playingPlayer.getPlayerGod().getGodType(), false);

        // and then apply opponent gods
        if(getPlayingPlayer().getOpponentsGodCards().size()>0)
        {
            Phase opponentgodphase1 = applyDecorator(playergodphase, getPlayingPlayer().getOpponentsGodCards().get(0).getGodType(), true);
            if(getPlayingPlayer().getOpponentsGodCards().size()>1)
            {
                Phase opponentgodphase2 = applyDecorator(opponentgodphase1, getPlayingPlayer().getOpponentsGodCards().get(1).getGodType(), true);
                phaseList.add(opponentgodphase2);
            }
            else
                phaseList.add(opponentgodphase1);
        }
        else
            phaseList.add(playergodphase);

        phaseList.get(phaseList.size()-1).startPhase(); //actually calls the method startPhase of the player's own decorator
    }

    /**
     * Method to call when applying a decorator
     * @param phasetodecorate phase to decorate
     * @param godType god with which to decorate the phase
     * @param bActAsOpponentGod boolean that indicates if the decorator is applied by an opponent
     * @return the decorated phase
     */

    public Phase applyDecorator(Phase phasetodecorate, GodCard.GodsType godType, boolean bActAsOpponentGod) {
        if (godType == GodCard.GodsType.Apollo) {
            return new ApolloDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Artemis) {
            return new ArtemisDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Atlas) {
            return new AtlasDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Demeter) {
            return new DemeterDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Hephaestus) {
            return new HephaestusDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Minotaur) {
            return new MinotaurDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Pan) {
            return new PanDecorator(phasetodecorate, bActAsOpponentGod);
        }
        if (godType == GodCard.GodsType.Prometheus) {
            return new PrometheusDecorator(phasetodecorate, bActAsOpponentGod);
        }

        return phasetodecorate;
    }

}