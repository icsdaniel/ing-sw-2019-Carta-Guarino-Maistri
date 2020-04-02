package it.polimi.ingsw.PSP027.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lorenzo Guarino
 * @author Elisa Maistri
 * */

public class Player {

    private String nickname;
    private List<Worker> playerWorkers;
    private GodCard playerGodCard;

    private List<GodCard> opponentsGodCards;



    /**
     * Constructor: creates a player, creating its workers
     */

    public Player() {
        playerWorkers = new ArrayList<Worker>();

        opponentsGodCards = new ArrayList<GodCard>();

        for (int i = 0; i < 2; i++) playerWorkers.add(new Worker(this, i));
    }

    public GodCard getPlayerGod() {
        return playerGodCard;
    }

    /**
     * Method to get the player's nickname
     * @return player's nickname
     * */

    public String getNickname() {
        return nickname;
    }

    /**
     * Method to set the player's nickname
     * @param nickname player nickname to set
     */

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Method to get the list of the player's workers
     * @return the player's workers
     */

    public List<Worker> getPlayerWorkers() {
        return playerWorkers;
    }

    /**
     * Method to add an opponent's god when it needs to decorate this player's next turn
     * @param opponentGod opponent god to add to the list (that will contain 0, 1 or 2 gods depending
     *                    on the number of players and if they influence their opponent's turn or not)
     */

    public void addOpponentGodCard(GodCard opponentGod) {
        opponentsGodCards.add(opponentGod);
    }

    /**
     * Method that clears the list of the opponent's gods.
     * It is going to be launched when this player's turn is over in order to have next turn not
     * influenced by its opponent's gods unless they need to (in that case their god will be read to the list with setOpponentGod()
     */

    public void removeOpponentGodCards() {
        opponentsGodCards.clear();
    }

    /**
     * Method that starts the turn played by this player
     * */

    public void playTurn() {
    }

    /**
     * Method that returns the list of opponent's gods that will influence this player's next turn
     * @return the list of opponent's gods. Can be null if no opponent player have a god card that influence
     * an opponent's turn
     */

    public List<GodCard> getOpponentsGodCards() {
        return opponentsGodCards;
    }

    public void setPlayerGodCard(GodCard playerGodCard) {
        this.playerGodCard = playerGodCard;
    }

    /*
    /**
     * PROBABILMENTE NON ANDRÁ QUI
     * Method that instantiate the decorator of the player's god
     * @param godToBuild god whose decorator needs to be instantiated

    public void buildDecorator(GodCard godToBuild) {
        if (playerGod.getGodName().equals("Apollo")) { new ApolloDecorator(); }
        if (playerGod.getGodName().equals("Artemis")) { new ArtemisDecorator(); }
        if (playerGod.getGodName().equals("Atlas")) { new AtlasDecorator(); }
        if (playerGod.getGodName().equals("Demeter")) { new DemeterDecorator(); }
        if (playerGod.getGodName().equals("Hephaestus")) { new HephaestusDecorator(); }
        if (playerGod.getGodName().equals("Minotaur")) { new MinotaurDecorator(); }
        if (playerGod.getGodName().equals("Pan")) { new PanDecorator(); }
        if (playerGod.getGodName().equals("Prometheus")) { new PrometheusDecorator(); }
    }
     */

}