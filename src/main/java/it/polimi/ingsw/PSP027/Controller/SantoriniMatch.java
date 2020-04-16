package it.polimi.ingsw.PSP027.Controller;

import it.polimi.ingsw.PSP027.Model.Game.Board;
import it.polimi.ingsw.PSP027.Model.Game.GodCard;
import it.polimi.ingsw.PSP027.Model.Game.Player;
import it.polimi.ingsw.PSP027.Model.Game.Worker;
import it.polimi.ingsw.PSP027.Model.Gods.GodPowerDecorator;
import it.polimi.ingsw.PSP027.Model.Gods.MinotaurDecorator;
import it.polimi.ingsw.PSP027.Model.TurnsManagement.Turn;
import it.polimi.ingsw.PSP027.Model.TurnsManagement.MovePhase;
import it.polimi.ingsw.PSP027.Network.ProtocolTypes;
import it.polimi.ingsw.PSP027.View.CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Elisa Maistri
 * @author Lorenzo Guarino
 * @author danielecarta
 * */

public class SantoriniMatch implements Runnable{

    /**
     * This class manages the game's match. It has the board of the game, the list of players,
     * the turns played in order to save them, the list of the gods and a godsInUse list used by the first
     * player to choose the gods they will play with (from godsList) and from which each player will choose
     * their own god card.
     */

    private Board gameBoard;
    private int requiredPlayers;
    private List<Player> players;
    private List<Turn> playedTurns;
    private UUID matchID;
    private List<GodCard> godCardsList;
    private List<GodCard> godCardsInUse;
    private boolean matchStarted;
    private boolean matchEnded;

    /**
     * Constructor: this creates a new match, creating a list for the players that will the be filled as the players are added,
     * the same for the list of turns, the gods that will be used in the match and the list of all gods, generates the random unique
     * id of the match, creates a board and sets the variable that tells if the match has started to false
     */

    public SantoriniMatch() {
        matchID = UUID.randomUUID();
        requiredPlayers = 2;
        players = new ArrayList<Player>();
        playedTurns = new ArrayList<Turn>();
        godCardsInUse = new ArrayList<GodCard>();
        gameBoard = new Board();
        matchStarted = false;
        matchEnded = false;
        godCardsList = new ArrayList<GodCard>();


        godCardsList.add(new GodCard(GodCard.GodsType.Apollo, GodCard.APOLLO_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Artemis, GodCard.ARTEMIS_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Athena, GodCard.ATHENA_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Atlas, GodCard.ATLAS_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Demeter, GodCard.DEMETER_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Hephaestus, GodCard.HEPHAESTUS_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Minotaur, GodCard.MINOTAUR_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Pan, GodCard.PAN_D));
        godCardsList.add(new GodCard(GodCard.GodsType.Prometheus, GodCard.PROMETHEUS_D));
    }

    @Override
    public void run() {
        // here goes what SantoriniMatch does, so the controller part
        try {
            while(!matchEnded) {

                if (matchStarted) {

                    // manage game

                } else {
                    TimeUnit.MILLISECONDS.sleep(200);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to use in order to check if there are no players in the match
     * @return true if the match has players, otherwise false
     */

    public boolean hasPlayers() { return !players.isEmpty(); }

    /**
     * Method to get the number of players currently in this match
     * @return the number of players in the match
     */

    public int numberOfPlayers() { return players.size(); }

    /**
     * Method to get the id of this match
     * @return the match's id
     */

    public UUID getMatchId () { return matchID; }

    /**
     * Method that checks if the match has already started
     * @return true if it has started, otherwise false
     */

    public boolean isStarted() { return matchStarted; }

    /**
     * Method to get the first player of the list of players
     * @return the first player
     */

    public Player getFirstPlayer() { return players.get(0); }

    /**
     * Method to get the Board of the game
     * @return the board
     */

    public Board getGameBoard() {
        return gameBoard;
    }

    /**
     * Method that changes the list of players in order to rotate them.
     * EXAMPLE: players A , B and C will be rotated to B, C and A.
     * The players will be rotated with each turn in order to have each time as the first player the one that is playing the turn
     */

    public void rotatePlayers(){

        Player p = players.get(0);
        players.remove(p);
        players.add(p);
    }

    /**
     * Method that sets the list of players
     * @param players players to set as the match's players
     */

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Method to get the list of players in the match
     * @return the list of players
     */

    public List<Player> getPlayers() { return players; }

    /**
     * Method to get the list of god cards
     * @return the list of god cards
     */

    public List<GodCard> getGodCardsList() { return godCardsList; }

    /**
     * Method to get the number of players that will play this match
     * @return the required players, which can be 2 or 3
     */
    public int GetRequiredNumberOfPlayers()  { return requiredPlayers; }

    /**
     * Method that sets the required number of players as the number of players that will play in the match (chosen by the gamer)
     * @param playersCount number of players that this match will require
     */

    public void SetRequiredNumberOfPlayers(int playersCount) {
        if(!matchStarted)
            requiredPlayers = playersCount;
    }


    /* ****************************************************************************************************
     * METHODS WITH WHICH THE SEND COMMAND CYCLE BEGINS
     ******************************************************************************************************/

    /**
     * Method used to add a player to the match, if it has not started yet
     * @param player is the player that is to be added to the list of players
     */

    public synchronized void addPlayer(Player player) {
        if(matchStarted)
            return;

        players.add(player);

        String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_EnteringMatch.toString() + "</id><data><players>";
        for(int i = 0; i < players.size(); i++){

            cmd += "<player>" + players.get(i).getNickname() + "</player>";
        }

        cmd += "</players></data></cmd>";

        System.out.println("SantoriniMatch addPlayer cmd: " + cmd);


        for(int i = 0; i < players.size(); i++){

            players.get(i).SendCommand(cmd);
        }

        if(players.size() == requiredPlayers)
            startGame();
    }

    /**
     * Method that removes the playerToRemove and all his attributes from the game
     * @param playerToRemove
     */
    public synchronized void removePlayer(Player playerToRemove) {

        if (matchStarted) {

            for (int i = 0; i < playerToRemove.getPlayerWorkers().size(); i++) {
                playerToRemove.getPlayerWorkers().get(i).changePosition(null);
            }

            GodCard cardToRemove = playerToRemove.getPlayerGod();

            for (int j = 1; j < players.size(); j++) {
                Player tempPlayer = players.get(j);
                for (int p = 0; p < tempPlayer.getOpponentsGodCards().size(); p++) {
                    if (tempPlayer.getOpponentsGodCards().get(p).getGodName().equals(cardToRemove.getGodName())) {
                        tempPlayer.getOpponentsGodCards().remove(tempPlayer.getOpponentsGodCards().get(p));
                    }
                }
            }

            playerToRemove.removeOpponentGodCards();

            // notify removed player of having lost game
            String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_Loser.toString()  + "</id></cmd>";
            System.out.println("SantoriniMatch removePlayer notify loser players cmd: " + cmd);
            playerToRemove.SendCommand(cmd);

            players.remove(playerToRemove);

            // notify remainig players that one player has been removed
            cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_LeftMatch.toString()  + "</id><data><player>" + playerToRemove.getNickname() + "</player></data></cmd>";
            System.out.println("SantoriniMatch removePlayer notify remaining players of player removal cmd: " + cmd);

            for (Player player : players) {

                player.SendCommand(cmd);
            }

            checkLoseCondition(players);
        }
        else{

            players.remove(playerToRemove);

            String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_EnteringMatch.toString()  + "</id><data><players>";
            for(int i = 0; i < players.size(); i++){

                cmd += "<player>" + players.get(i).getNickname() + "</player>";
            }

            cmd += "</players></data></cmd>";

            System.out.println("SantoriniMatch removePlayer cmd: " + cmd);

            for (Player player : players) {

                player.SendCommand(cmd);
            }
        }
    }

    /**
     * Method that does the setup of the game
     */

    public void startGame(){
        matchStarted = true;

        // notify players that game has started
        String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_EnteredMatch.toString()  + "</id><data><players>";
        for(int i = 0; i < players.size(); i++){

            cmd += "<player>" + players.get(i).getNickname() + "</player>";
        }

        cmd += "</players></data></cmd>";

        System.out.println("SantoriniMatch startGame cmd: " + cmd);

        for(int i = 0; i < players.size(); i++){

            players.get(i).SendCommand(cmd);
        }

        saveGame();

        // perform starting game stuffs here: choose gods card at first

        cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_ChooseGods.toString()  + "</id><data><requiredgods>" + Integer.toString(requiredPlayers) + "</requiredgods><gods>";
        for(int i = 0; i < godCardsList.size(); i++){

            cmd += "<god>" + godCardsList.get(i).getGodName() + "</god>";
        }

        cmd += "</gods></data></cmd>";

        players.get(0).SendCommand(cmd);

    }

    /**
     * Method that ends the game if the win conditions are verified
     * @TODO tell lobby to handle the ending of the match
     */

    public void endGame(Player playerWinner) {
        System.out.println("Player " + playerWinner.getNickname() + " has Won!");

        // send to all players the winner one
        String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_Winner.toString()  + "</id><data><player>" + playerWinner.getNickname() + "</player></data></cmd>";

        for(int i = 0; i < players.size(); i++){

            players.get(i).SendCommand(cmd);
        }

    }

    /**
     * Method that sets the god cards in use with the god cards chosen by the user (2 or 3 cards depending of the required number of players of the match
     * @param chosengods list of the names of the gods chosen by the user
     */

    public void setGodCardsInUse (List<String> chosengods) {
        for (String chosengod : chosengods) {
            for (GodCard god : godCardsList) {
                if(god.getGodName().equals(chosengod)) {
                    godCardsInUse.add(god);
                    break;
                }
            }
        }

        rotatePlayers();

        saveGame();

        String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_ChooseGod.toString()  + "</id><data><gods>";
        for(int i = 0; i < godCardsInUse.size(); i++){

            cmd += "<god>" + godCardsInUse.get(i).getGodName() + "</god>";
        }

        cmd += "</gods></data></cmd>";

        players.get(0).SendCommand(cmd);
    }

    /**
     * Method that assigns the chosen god card by the user to its player's god card
     * @param player player that has chosen the god card
     * @param chosengod name of the god that the player has chosen
     */

    public void setGodCardForPlayer (Player player, String chosengod) {

        for(GodCard god : godCardsInUse) {
            if(god.getGodName().equals(chosengod)) {
                godCardsInUse.remove(god);
                player.setPlayerGodCard(god);
                break;
            }
        }

        rotatePlayers();

        saveGame();

        if(players.get(0).getPlayerGod() == null) {
            String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_ChooseGod.toString()  + "</id><data><gods>";
            for(int i = 0; i < godCardsInUse.size(); i++){

                cmd += "<god>" + godCardsInUse.get(i).getGodName() + "</god>";
            }

            cmd += "</gods></data></cmd>";
            players.get(0).SendCommand(cmd);
        }
        else {
            // all players have a god card. START HERE TO ASK TO CHOOSE FOR THE FIRST PLAYER
            // NOTE: sice we should send the command to the master player, that was the last one
            // choosing god card, actually this user is the last in the list of players
            // due to former rotatePlayers(). so we need to rotate list to put him in front
            rotatePlayers();

            if(players.size()==3)
                rotatePlayers();

            String cmd = "<cmd><id>" + ProtocolTypes.protocolCommand.srv_ChooseFirstPlayer.toString()  + "</id><data><players>";
            for(int i = 0; i < players.size(); i++){

                cmd += "<player>" + players.get(i).getNickname() + "</player>";
            }

            cmd += "</players></data></cmd>";
            players.get(0).SendCommand(cmd);

        }
    }

    /**
     * Method that sets the chosen first player by the user as the first player of the game
     * @param chosenplayer name of the player that the player has chosen
     */

    public void setFirstPlayer(String chosenplayer) {

        // rotate players till the first one matches given one
        while(true) {
            if(chosenplayer.equals(players.get(0).getNickname())) {
                break;
            }
            else {
                rotatePlayers();
            }
        }

        saveGame();

        // The first player who is going to place the workers (and then start the game) is set as the first player in the list of players.
        // Start here the process of asking to place the workers

        SendCurrentBoardToPlayerWithGivenCommand(players.get(0), ProtocolTypes.protocolCommand.srv_ChooseWorkerStartPosition);
    }


    /**
     * Method that sets the chosen starting positions of a player and updates the board
     * @param chosenpositions positions chosen where to place the workers
     * @TODO start game
     */

    public void setWorkersStartPosition(Player player, List<String> chosenpositions) {

        for(int i = 0; i < 2; i++) {

            int chosencellindex;

            chosencellindex = (chosenpositions.get(i).charAt(0) - 'A') * 5 + (chosenpositions.get(i).charAt(1) - '1');

            player.getPlayerWorkers().get(i).setPosition(gameBoard.getCell(chosencellindex));
            gameBoard.getCell(chosencellindex).setWorkerOccupying(player.getPlayerWorkers().get(i));
        }

        rotatePlayers();

        saveGame();

        if(players.get(0).getPlayerWorkers().get(0).getWorkerPosition() == null) {

            SendCurrentBoardToPlayerWithGivenCommand(players.get(0), ProtocolTypes.protocolCommand.srv_ChooseWorkerStartPosition);
        }
        else {

            //ALL PLAYERS HAVE PLACED THEIR WORKERS. START TURNS

        }

    }

    private void SendCurrentBoardToPlayerWithGivenCommand(Player player, ProtocolTypes.protocolCommand command)
    {
        String cmd = "<cmd><id>" + command.toString()  + "</id><data><board>";

        for(int i = 0; i < gameBoard.getBoard().size(); i++) {
            cmd += "<cell id=\"" + Integer.toString(i) +
                    "\" level=\"" + Integer.toString(gameBoard.getCell(i).getLevel()) +
                    "\" dome=\"" + Boolean.toString(gameBoard.getCell(i).checkDome());


            if(gameBoard.getCell(i).isOccupiedByWorker()) {
                cmd += "\" nickname=\"" + gameBoard.getCell(i).getOccupyingWorker().getWorkerOwner().getNickname();
            }
            else {
                cmd += "\" nickname=\"";
            }

            cmd += "\" />";
        }

        cmd += "</board></data></cmd>";

        player.SendCommand(cmd);
    }

    /* ***************************************************************************************** */

    /**
     * Method that creates a new turn for the first player in the list
     * @return
     */

    public Turn newTurn(){
        return new Turn(this.getFirstPlayer(),this);
    }

    /**
     * Method that decorates the player's turn according to its GodCard
     * @return
     */

    public GodPowerDecorator decorateTurn(Turn turn) {
        switch (turn.getPlayingPlayer().getPlayerGod().getGodType()){
            //case Apollo: break;
            //case Artemis: break;
            //case Athena: break;
            //case Atlas: break;
            //case Demeter: break;
            //case Hephaestus: break;
            //case Minotaur: return null; //new MinotaurDecorator(turn);
            //case Pan: break;
            //case Prometheus: break;
        }
        return null;
    }

    /**
     * Method that saves the last turn in playedTurns
     * @param
     * @param
     */

    public void saveGame() {

        // save an xml file with all data members of SantoriniMatch.
        // the file name will be the UUID of the match
        //
        // godCardsList does not have to be saved since it is a fixed structure
        // matchStarted is always true if game has been saved
        //
        // <SantoriniMatch matchID requiredPlayers>
        // <board><cell .../>...<cell ... /></board>
        // <players><player>....</player>...<player>....</player></players>
        // <godCardsInUse><godcard></godcard>...<godcard></godcard></godCardsInUse>
        // <turns><turn>...</turn>....<turn>...</turn></turns>
        // </SantoriniMatch>

    }

    /**
     * Method that resumes the game by restarting at last turn saved
     * @param
     * @param
     */

    public void resumeGame() {
        // read saved game, using UUID to get the file
        // process xml data to recreate SantoriniMatch structure.
        // then processing the data recreated, we can resume game at the time it was left. for example:
        // if playedTurns is not empty, the last Turn in the list was the last one played (so last player playing is known)
        // if godCardsInUse is empty, we know we should start asking to choose cards
        // if godCardsInUse is not empty, checking players godcard chosen or workers position we can determine if all players had
        // selected the god card and/or positioned workers ....


    }
/*
    /**
     * Method that checks is the Win condition are verified, in this case end the game
     * @param currentTurn the last ConcreteTurn, currently playing
     * @param lastMovePhase the last MovePhase, currently playing


    public void checkWinCondition(Turn currentTurn, MovePhase lastMovePhase){
        if(lastMovePhase.getStartChosenWorkerLvl() == 2 && currentTurn.getChosenWorker().getWorkerPosition().getLevel() == 3){
            endGame(currentTurn.getPlayingPlayer());
        }
    }*/

    /**
     *
     * @param playersInGame
     */

    public void checkLoseCondition(List<Player> playersInGame){
        if(playersInGame.size() == 1){
            endGame(playersInGame.get(0));
        }
    }
}
