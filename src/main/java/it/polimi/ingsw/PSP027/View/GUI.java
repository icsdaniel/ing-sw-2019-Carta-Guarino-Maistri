package it.polimi.ingsw.PSP027.View;

import it.polimi.ingsw.PSP027.Network.Client.Client;
import it.polimi.ingsw.PSP027.Network.Client.ClientObserver;
import it.polimi.ingsw.PSP027.View.Controllers.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.*;

public class GUI extends Application implements ClientObserver {


    public Client client = null;
    private int requiredgods = 0;
    private List<String> gods = null;
    private List<String> players = null;
    private Node nodeboard; //it's overwritten every time a new board needs to be printed
    private List<Integer> indexcandidatecells = new ArrayList<Integer>(); //used for move and build and is overwritten every time
    private Map<String, String> PlayerGodMap = new HashMap<String, String>();
    private Map<String, String> PlayerWorkerMap = new HashMap<String, String>();
    private Stage SantoriniStage;
    private String firstPlayersGod;
    private List<String> deadPlayers = new ArrayList<String>();
    private String playingPlayerNickname;
    private Phase currentPhase;
    private BoardPage_UniqueController BoardController;
    private Parent BoardPage=null;
    public enum Phase {
        Build,
        PlaceWorkers,
        ChooseWorker,
        Move,
        OptBuild,
        OptMove,
        OptEnd,
        Update
    }

    /* ****************************************************** COMMANDS ************************************************** */

    private static String DISCONNECT_COMMAND = "disconnect";
    private static String BYE_COMMAND = "bye";
    private static String CONNECT_COMMAND = "connect";
    private static String REGISTER_COMMAND = "register";
    private static String DEREGISTER_COMMAND = "deregister";
    private static String SEARCHMATCH_COMMAND = "searchmatch";
    private static String CHOSENGODS_COMMAND = "chosengods";
    private static String CHOSENGOD_COMMAND = "chosengod";
    private static String CHOSENFIRSTPLAYER_COMMAND = "firstplayerchosen";
    private static String PLAY_COMMAND = "play";
    private static String WORKERSPOSITION_COMMAND = "workerspositionchosen";
    private static String CHOSENWORKER_COMMAND = "workerchosen";
    private static String CANDIDATECELLFORMOVE_COMMAND = "candidatecellchosen";
    private static String CANDIDATECELLFORBUILD_COMMAND = "candidatebuildcell";
    private static String CANDIDATECELLFOREND_COMMAND = "candidateendcell";
    private static String PASSMOVE_COMMAND = "movepassed";
    private static String PASSBUILD_COMMAND = "buildpassed";
    private static String PASSEND_COMMAND = "endpassed";

    /* ******************************************************************************************************************* */

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        client = new Client();
        client.addObserver(this);
        Thread clientThread = new Thread(client);
        clientThread.start();

        SantoriniStage = stage;

        SantoriniStage.setTitle("Santorini"); //name of the game window that is shown

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EntryPage.fxml"));
        Parent entryPage = (Parent) loader.load();

        EntryPageController ctrl = loader.getController();
        ctrl.setGui(this);

        Scene entryScene = new Scene(entryPage, 1800, 850);
        SantoriniStage.setMinHeight(900);
        SantoriniStage.setMinWidth(1440);
        SantoriniStage.setMaximized(true);
        SantoriniStage.setFullScreen(true);
        SantoriniStage.setScene(entryScene);
        SantoriniStage.show();

        SantoriniStage.setOnCloseRequest(e->{
            System.out.println("Closing Santorini Game");
            Platform.exit();
            System.exit(0);
        });


    }

    public String getWorkerUrlImageGivenTheNickname (String nickname) {
        return PlayerWorkerMap.get(nickname);
    }

    public String getGodGivenTheNickname (String nickname) {
        return PlayerGodMap.get(nickname);
    }

    public Stage getSantoriniStage() {
        return SantoriniStage;
    }

    /**
     * Method that load the Connect fxml page and set the new Scene in SantoriniStage
     */
    public void showConnectedPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ConnectedPage.fxml"));
            Parent connectedPage = (Parent) loader.load();

            ConnectedController connectedController = loader.getController();
            connectedController.setGui(this);

            SantoriniStage.getScene().setRoot(connectedPage);
            SantoriniStage.show();

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }

    public void showServerConnectionClosed() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The connection was closed.");
        alert.setTitle("THE SERVER CONNECTION WAS CLOSED");
        alert.setHeaderText("The server connection was closed. You can connect again.");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(SantoriniStage);
        Optional<ButtonType> result = alert.showAndWait();
    }

    public void showServerHasDied() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The connection was closed.");
        alert.setTitle("THE SERVER HAS DIED");
        alert.setHeaderText("The server has died, we are sorry.");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(SantoriniStage);
        Optional<ButtonType> result = alert.showAndWait();
    }
    /**
     * Method that load the EntryPage fxml page and set the new Scene in SantoriniStage
     */
    public void showEntryPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EntryPage.fxml"));
            Parent entryPage = (Parent) loader.load();

            EntryPageController entryPageController = loader.getController();
            entryPageController.setGui(this);

            SantoriniStage.getScene().setRoot(entryPage);
            SantoriniStage.show();

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the Register fxml page, sets the nickname of the player and sets the new Scene in SantoriniStage
     */
    public void showRegisteredPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/RegisteredPage.fxml"));
            Parent registeredPage = (Parent) loader.load();

            RegisteredController registeredController = loader.getController();
            registeredController.setGui(this);
            registeredController.setNickname(client.getNickname());

            SantoriniStage.getScene().setRoot(registeredPage);
            SantoriniStage.show();

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    public void showRegistrationError(String error) {

        switch(error) {
            case "Nickname already present": {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please try again with another nickname");
                alert.setTitle("REGISTRATION ERROR");
                alert.setHeaderText("There was an error with your registration: your chosen nickname is already taken.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(SantoriniStage);
                Optional<ButtonType> result = alert.showAndWait();
                break;
            }
            case "Missing nickname": {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please try again with a valid nickname");
                alert.setTitle("REGISTRATION ERROR");
                alert.setHeaderText("There was an error with your registration: please enter a valid nickname and try again.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(SantoriniStage);
                Optional<ButtonType> result = alert.showAndWait();
                break;
            }
        }
    }
    /**
     * Method that load the EnteringMatch fxml page, sets the nickname of the players and set the new Scene in SantoriniStage
     */
    public void showEnteringMatchPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EnteringMatchPage.fxml"));
            Parent enteringMatchPage = (Parent) loader.load();

            EnteringMatchController enteringMatchController = loader.getController();
            enteringMatchController.setGui(this);
            enteringMatchController.setNickname(players);

            SantoriniStage.getScene().setRoot(enteringMatchPage);
            SantoriniStage.show();

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the ChooseGods fxml page and set the new Scene in SantoriniStage
     */
    public void showChooseGodsPage() {
        try {
            System.out.println("showChooseGodsPage IN");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChooseGodsPage.fxml"));
            Parent chooseGodsPage = (Parent) loader.load();

            ChooseGodsController chooseGodsController = loader.getController();
            chooseGodsController.setGui(this);
            chooseGodsController.setChooseGodsTitle(requiredgods);

            SantoriniStage.getScene().setRoot(chooseGodsPage);
            SantoriniStage.show();

            System.out.println("showChooseGodsPage OUT");

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the ChooseYourGodCase3  fxml page (the 3 indicate the number of players), sets the correct gods in the scene
     * and sets the new Scene in SantoriniStage
     */
    public void showChooseYourGodPageCase3() {
        try {
            System.out.println("showChooseYourGodPageCase3 IN");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChooseYourGodPageCase3.fxml"));
            Parent chooseYourGodsPageCase3 = (Parent) loader.load();

            ChooseYourGodCase3Controller chooseYourGodCase3Controller = loader.getController();
            chooseYourGodCase3Controller.setGui(this);
            chooseYourGodCase3Controller.setChooseGodTitle(this.gods);

            SantoriniStage.getScene().setRoot(chooseYourGodsPageCase3);
            SantoriniStage.show();

            System.out.println("showChooseYourGodPageCase3 OUT");

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the ChooseYourGodCase2  fxml page (the 2 indicate the number of players), sets the correct gods in the scene
     * and sets the new Scene in SantoriniStage
     */
    public void showChooseYourGodPageCase2(){
        try {
            System.out.println("showChooseYourGodPageCase2 IN");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChooseYourGodPageCase2.fxml"));
            Parent chooseYourGodsPageCase2 = (Parent) loader.load();

            ChooseYourGodCase2Controller chooseYourGodCase2Controller = loader.getController();
            chooseYourGodCase2Controller.setGui(this);
            chooseYourGodCase2Controller.setChooseGodTitle(this.gods);

            SantoriniStage.getScene().setRoot(chooseYourGodsPageCase2);
            SantoriniStage.show();

            System.out.println("showChooseYourGodPageCase2 OUT");

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the Waiting fxml page, sets the waiting message in the scene
     * and sets the new Scene in SantoriniStage
     * @param waitingMessage message
     */
    public void showWaitingPage(String waitingMessage){
        try {
            System.out.println("showWaitingPage IN");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Waiting.fxml"));
            Parent waitingPage = (Parent) loader.load();

            WaitingController waitingController = loader.getController();
            waitingController.setGui(this);
            waitingController.setWaitingMessage(waitingMessage);

            SantoriniStage.getScene().setRoot(waitingPage);
            SantoriniStage.show();

            System.out.println("showWaitingPage OUT");

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the ChooseFirstPlayerCase2 fxml page (the 2 indicate the number of players), sets the nicknames and god of
     *  the player in the scene, and sets the new Scene in SantoriniStage
     */
    public void showChooseFirstPlayerPageCase2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChooseFirstPlayerCase2.fxml"));
            Parent chooseFirstPlayerCase2Page = (Parent) loader.load();

            ChooseFirstPlayerCase2Controller chooseFirstPlayerCase2Controller = loader.getController();
            chooseFirstPlayerCase2Controller.setGui(this);
            chooseFirstPlayerCase2Controller.setNickname(players);
            chooseFirstPlayerCase2Controller.setGod(firstPlayersGod);

            SantoriniStage.getScene().setRoot(chooseFirstPlayerCase2Page);
            SantoriniStage.show();

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the ChooseFirstPlayerCase3 fxml page (the 3 indicate the number of players), sets the nicknames and god of
     *  the player in the scene, and sets the new Scene in SantoriniStage
     */
    public void showChooseFirstPlayerPageCase3() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChooseFirstPlayerCase3.fxml"));
            Parent chooseFirstPlayerCase3Page = (Parent) loader.load();

            ChooseFirstPlayerCase3Controller chooseFirstPlayerCase3Controller = loader.getController();
            chooseFirstPlayerCase3Controller.setGui(this);
            chooseFirstPlayerCase3Controller.setNickname(players);
            chooseFirstPlayerCase3Controller.setGod(firstPlayersGod);

            SantoriniStage.getScene().setRoot(chooseFirstPlayerCase3Page);
            SantoriniStage.show();

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }

    /**
     * Method that load the UniqueBoard fxml page only the first time that is called, then it will no longer create new controllers
     * or load new pages. Every action made in every phase of the turn call this method for updating the scene in SantoriniStage.
     */
    public void showBoardPage_UniqueBoard(){
            if(BoardPage==null){
                loadBoardPage();
             }

            BoardController.resetBoardGrid();
            Node cell;
            BoardController.setPhaseName();

            if (nodeboard.hasChildNodes()) {
                NodeList cells = nodeboard.getChildNodes();

                for (int i = 0; i < cells.getLength(); i++) {
                    cell = cells.item(i);

                    if (cell.getNodeName().equals("cell")) {
                        int id = getIdOfCellNode(cell);
                        int level = getLevelOfCellNode(cell);
                        boolean dome = getDomeOfCellNode(cell);

                        String nickname = getNicknameOfCellNode(cell);

                        if(level!=0) {
                            BoardController.setLevel(id, level);
                        }

                        if (dome) {
                            BoardController.setDome(id);
                        }

                        if (!nickname.isEmpty()) {
                            BoardController.setWorker(id, PlayerWorkerMap.get(nickname));
                        }

                        if (!indexcandidatecells.isEmpty()) {
                            for (Integer indexcandidatecell : indexcandidatecells) {
                                if (indexcandidatecell == id) {
                                    BoardController.setCandidate(id);
                                    break;
                                }
                            }
                        }
                    }
                }

                if(this.currentPhase == Phase.PlaceWorkers) {
                    BoardController.setPlacingWorkersBoard();
                }

                if (PlayerGodMap.size() > 0) {
                    Set<String> nicknames = PlayerGodMap.keySet();
                    String nickname;
                    Iterator<String> itr = nicknames.iterator();
                    int playerscount = 1;
                    boolean playingPlayer = false;

                    while (itr.hasNext()) {
                        nickname = itr.next();
                        if (PlayerWorkerMap.containsKey(nickname) && PlayerGodMap.containsKey(nickname)) {
                            if(nickname.equals(playingPlayerNickname)) {
                                playingPlayer = true;
                            }

                            if(playerscount == 1) {
                                boolean deadPlayer = false;
                                for (String player : deadPlayers) {
                                    if (player.equals(nickname)) {
                                        deadPlayer = true;
                                        break;
                                    }
                                }
                                BoardController.setPlayer1Panel(PlayerGodMap.get(nickname), nickname, playingPlayer, deadPlayer, PlayerWorkerMap.get(nickname));
                            } else if(playerscount == 2) {
                                boolean deadPlayer = false;
                                for (String player : deadPlayers) {
                                    if (player.equals(nickname)) {
                                        deadPlayer = true;
                                        break;
                                    }
                                }
                                BoardController.setPlayer2Panel(PlayerGodMap.get(nickname), nickname, playingPlayer, deadPlayer, PlayerWorkerMap.get(nickname));
                            } else if(playerscount == 3) {
                                boolean deadPlayer = false;
                                for (String player : deadPlayers) {
                                    if (player.equals(nickname)) {
                                        deadPlayer = true;
                                        break;
                                    }
                                }
                                BoardController.setPlayer3Panel(PlayerGodMap.get(nickname), nickname, playingPlayer, deadPlayer, PlayerWorkerMap.get(nickname));
                            }
                        }
                        playerscount++;
                        playingPlayer = false;
                    }
                }

                if(players.size() == 2) {
                    BoardController.setPanel3Visibility(false);
                }
                else {
                    BoardController.setPanel3Visibility(true);
                }
            }

            SantoriniStage.getScene().setRoot(BoardPage);
            SantoriniStage.show();

    }
    /**
     * Method that load the YouHaveWon fxml page, sets the nickname of the winner
     * and sets the new Scene in SantoriniStage
     */
    public void showYouHaveWonPage(){
        try {
            System.out.println("showYouHaveWonPage IN");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/YouHaveWonPage.fxml"));
            Parent youHaveWonPage = (Parent) loader.load();

            YouHaveWonController youHaveWonController = loader.getController();
            youHaveWonController.setGui(this);
            String godWinner = PlayerGodMap.get(client.getNickname());
            youHaveWonController.setWinnerPodium(godWinner);
            youHaveWonController.setWinnerCongrats(client.getNickname());

            SantoriniStage.getScene().setRoot(youHaveWonPage);
            SantoriniStage.show();

            System.out.println("showYouHaveWonPage OUT");

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }
    /**
     * Method that load the YouHaveLost fxml page, sets the nickname of the loser
     * and sets the new Scene in SantoriniStage
     * @param winner winner's username
     */
    public void showYouHaveLostPage(String winner){
        try {
            System.out.println("showYouHaveLostPage IN");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/YouHaveLostPage.fxml"));
            Parent youHaveLostPage = (Parent) loader.load();

            YouHaveLostController youHaveLostController = loader.getController();
            youHaveLostController.setGui(this);

            if(!winner.isEmpty()) {
                youHaveLostController.setBetterLuckNextTimeWithWinner(winner);
            } else {
                youHaveLostController.setBetterLuckNextTimeWithoutWinner();
            }

            if(client.getNickname() != null && !gods.isEmpty() && !PlayerGodMap.isEmpty()) {
                String godLoser = PlayerGodMap.get(client.getNickname());
                youHaveLostController.setLoserPodium(godLoser);
            }

            SantoriniStage.getScene().setRoot(youHaveLostPage);
            SantoriniStage.show();

            System.out.println("showYouHaveLostPage OUT");

        }catch (IOException exception){
            System.out.println(exception.toString());
        }
    }

    /**
     * Method that is called by showUniqueBoardPage to load the UniqueBoard fxml page for the first time,
     * then it will be saved and reused by all next calls of the method
     */
    public void loadBoardPage(){
        try {
            if(this.currentPhase==null){
                this.currentPhase = Phase.PlaceWorkers;
            }
            FXMLLoader boardloader = new FXMLLoader(getClass().getResource("/views/BoardPage_UniqueBoard.fxml"));
            this.BoardPage = (Parent) boardloader.load();
            this.BoardController = boardloader.getController();
            this.BoardController.setGui(this);
        } catch (IOException exception){
            System.out.println(exception.toString());
        }
    }

    /* ***************************************************************************************************************
     *                    Methods fired by the client's methods that trigger the change of the view                  *
     *****************************************************************************************************************/

    /**
     * Method of the ClientObserver interface that is fired by the client after connection
     */

    @Override
    public void OnConnected(){
        System.out.println("OnConnected IN");
        Platform.runLater(() -> showConnectedPage());
        System.out.println("OnConnected OUT");
    }

    /**
     * Method of the ClientObserver interface that is fired by the client if there was an error when trying to connect
     */

    @Override
    public void OnConnectionError() {
    }

    /**
     * Method of the ClientObserver interface that is fired by the client if it has been disconnected from the server
     */

    @Override
    public void OnDisconnected() {
        System.out.println("OnDisconnected IN");
        Platform.runLater(() -> showEntryPage());
        System.out.println("OnDisconnected OUT");
    }

    /**
     * Method of the ClientObserver interface that is fired by the client if the server connection was closed
     */

    @Override
    public void OnServerConnectionClosed() {
        Platform.runLater(() -> showServerConnectionClosed());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client if the server has died
     */

    @Override
    public void OnServerHasDied() {
        Platform.runLater(() -> showServerHasDied());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client after the user has registered
     */

    @Override
    public void OnRegistered() {
        System.out.println("OnRegistered IN");
        Platform.runLater(() -> showRegisteredPage());
        System.out.println("OnRegistered OUT");

    }

    /**
     * Method of the ClientObserver interface that is fired by the client if there was an error when trying to register
     */

    @Override
    public void OnRegistrationError(String error) {
        Platform.runLater(() -> showRegistrationError(error));
    }

    /**
     * Method of the ClientObserver interface that is fired by the client after the user has been deregistered
     */

    @Override
    public void OnDeregistered() {
        Platform.runLater(() -> showConnectedPage());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when a user leaves the match
     *
     * @param nickname nicknames of the user that left the match
     */

    @Override
    public void OnLeftMatch(String nickname) {
        deadPlayers.add(nickname);
    }

    /**
     * Method of the ClientObserver interface that is fired by the client after connection
     */
    @Override
    public void OnChooseMatchType() {
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when entering a match
     */

    @Override
    public void OnEnteringMatch(List<String> players) {
        System.out.println("OnEnteringMatch IN " + ((this.players != null) ? this.players.size() : 0));
        this.players = players;
        Platform.runLater(() -> showEnteringMatchPage());
        System.out.println("OnEnteringMatch OUT " + this.players.size());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when it has entered a match
     */

    @Override
    public void OnEnteredMatch(List<String> players) {
        System.out.println("OnEnteredMatch IN " + ((this.players != null) ? this.players.size() : 0));
        this.players = players;
        PlayerGodMap.clear();
        PlayerWorkerMap.clear();
        for (int i = 0; i < players.size(); i++) {
            if (i == 0) {
                PlayerWorkerMap.put(players.get(i), "images/Board/redWorker_Board.png");
            } else if (i == 1) {
                PlayerWorkerMap.put(players.get(i), "images/Board/blueWorker_Board.png");
            } else if (i == 2) {
                PlayerWorkerMap.put(players.get(i), "images/Board/violetWorker_Board.png");
            }

        }
        Platform.runLater(() -> showWaitingPage("Wait while the first player chooses the gods you will play with"));
        System.out.println("OnEnteredMatch OUT " + this.players.size());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the list of gods to use in the match
     *
     * @param requiredgods number of gods that the client must choose
     * @param gods         gods' names chosen by the user
     */

    @Override
    public void OnChooseGods(int requiredgods, List<String> gods) {
        System.out.println("OnChooseGods IN");
        this.requiredgods = requiredgods;
        this.gods = gods;
        Platform.runLater(() -> showChooseGodsPage());
        System.out.println("OnChooseGods OUT");
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the god to play with
     *
     * @param chosengods chosen gods to set as the gods managed by the cli that will communicate them to the user
     */

    @Override
    public void OnChooseGod(List<String> chosengods) {
        System.out.println("OnChooseGod IN");
        this.gods = chosengods;
        if(this.gods.size()==3){
            Platform.runLater(() -> showChooseYourGodPageCase3());
            System.out.println("OnChooseGod OUT");
        }else if(this.gods.size()==2){
            Platform.runLater(() -> showChooseYourGodPageCase2());
            System.out.println("OnChooseGod OUT");
        }
        else if(this.gods.size()==1) {
            firstPlayersGod = chosengods.get(0);
            doSendSelectedGod(chosengods.get(0));
        }

    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the first player that
     * will place its workers and start playing
     *
     * @param players players in the match from which the selection must be done
     */

    @Override
    public void OnChooseFirstPlayer(List<String> players) {
        System.out.println("OnChooseFirstPlayer IN");
        this.players = players;
        if(players.size() == 2) {
            Platform.runLater(() -> showChooseFirstPlayerPageCase2());
            System.out.println("OnChooseFirstPlayer OUT");
        }
        else if (players.size() == 3) {
            Platform.runLater(() -> showChooseFirstPlayerPageCase3());
            System.out.println("OnChooseFirstPlayer OUT");
        }
    }


    /**
     * Method of the ClientObserver interface that is fired by the client when placing the workers on the board for the first time
     *
     * @param nodes board in xml format that needs to be processed by the GUI when it prints the board
     *              it also contains the list of players with their gods that needs to be saved
     */

    @Override
    public void OnChooseWorkerStartPosition(NodeList nodes) {

        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("players")) {

                Node player;
                if (node.hasChildNodes()) {
                    NodeList players = node.getChildNodes();

                    for (int j = 0; j < players.getLength(); j++) {
                        player = players.item(j);

                        if (player.getNodeName().equals("player")) {
                            String playerNickname = player.getAttributes().getNamedItem("nickname").getTextContent();
                            String playerGod = player.getAttributes().getNamedItem("god").getTextContent();

                            PlayerGodMap.put(playerNickname, playerGod);
                        }
                    }
                }
            }
        }

        this.currentPhase = Phase.PlaceWorkers;
        this.playingPlayerNickname = this.client.getNickname();
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }


    /* ************************************* METHODS REGARDING THE COMMUNICATION WHEN THE TURN HAS STARTED ************************+ */

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the worker to play the turn with
     *
     * @param board board in xml format that needs to be processed by the GUI when it prints the board
     */

    @Override
    public void OnChooseWorker(Node board) {
        this.nodeboard = board;
        this.playingPlayerNickname = this.client.getNickname();
        this.currentPhase = Phase.ChooseWorker;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the cell to move the worker onto
     *
     * @param nodes cells where is possible to move the worker in xml format that needs to be processed by the GUI
     */
    @Override
    public void OnCandidateCellsForMove(NodeList nodes) {

        indexcandidatecells.clear();
        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("candidates")) {

                Node cell;
                if (node.hasChildNodes()) {
                    NodeList cells = node.getChildNodes();

                    for (int j = 0; j < cells.getLength(); j++) {
                        cell = cells.item(j);

                        if (cell.getNodeName().equals("cell")) {
                            int id = getIdOfCellNode(cell);
                            indexcandidatecells.add(id);
                        }
                    }
                }
            }
        }
        this.playingPlayerNickname = this.client.getNickname();
        this.currentPhase = Phase.Move;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the cell to move the worker onto
     *
     * @param nodes cells where is possible to move the worker in xml format that needs to be processed by the GUI
     */
    @Override
    public void OnCandidateCellsForOptMove(NodeList nodes) {

        indexcandidatecells.clear();
        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("candidates")) {

                Node cell;
                if (node.hasChildNodes()) {
                    NodeList cells = node.getChildNodes();

                    for (int j = 0; j < cells.getLength(); j++) {
                        cell = cells.item(j);

                        if (cell.getNodeName().equals("cell")) {
                            int id = getIdOfCellNode(cell);
                            indexcandidatecells.add(id);
                        }
                    }
                }
            }
        }
        this.playingPlayerNickname = this.client.getNickname();
        this.currentPhase = Phase.OptMove;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the cell to do the build on
     *
     * @param nodes cells where is possible to build on in xml format that needs to be processed by the GUI
     */
    @Override
    public void OnCandidateCellsForBuild(NodeList nodes) {

        indexcandidatecells.clear();
        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("candidates")) {

                Node cell;
                if (node.hasChildNodes()) {
                    NodeList cells = node.getChildNodes();

                    for (int j = 0; j < cells.getLength(); j++) {
                        cell = cells.item(j);

                        if (cell.getNodeName().equals("cell")) {
                            int id = getIdOfCellNode(cell);
                            indexcandidatecells.add(id);
                        }
                    }
                }
            }
        }
        this.playingPlayerNickname = this.client.getNickname();
        this.currentPhase = Phase.Build;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the cell to do the build on
     *
     * @param nodes cells where is possible to build on in xml format that needs to be processed by the GUI
     */
    @Override
    public void OnCandidateCellsForOptBuild(NodeList nodes) {

        indexcandidatecells.clear();
        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("candidates")) {

                Node cell;
                if (node.hasChildNodes()) {
                    NodeList cells = node.getChildNodes();

                    for (int j = 0; j < cells.getLength(); j++) {
                        cell = cells.item(j);

                        if (cell.getNodeName().equals("cell")) {
                            int id = getIdOfCellNode(cell);
                            indexcandidatecells.add(id);
                        }
                    }
                }
            }
        }
        this.playingPlayerNickname = this.client.getNickname();
        this.currentPhase = Phase.OptBuild;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when choosing the cell to do the end Phase
     *
     * @param nodes cells where is possible to do the end Phase on in xml format that needs to be processed by the GUI
     */
    @Override
    public void OnCandidateCellsForOptEnd(NodeList nodes) {

        indexcandidatecells.clear();
        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("candidates")) {

                Node cell;
                if (node.hasChildNodes()) {
                    NodeList cells = node.getChildNodes();

                    for (int j = 0; j < cells.getLength(); j++) {
                        cell = cells.item(j);

                        if (cell.getNodeName().equals("cell")) {
                            int id = getIdOfCellNode(cell);
                            indexcandidatecells.add(id);
                        }
                    }
                }
            }
        }
        this.playingPlayerNickname = this.client.getNickname();
        this.currentPhase = Phase.OptEnd;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when there's a winner
     *
     * @param nickname nickname of the winner
     */

    @Override
    public void OnWinner(String nickname) {
        System.out.println("OnWinner IN");
        if(nickname.equals(client.getNickname())) {
            //the winner is this client
            Platform.runLater(() -> showYouHaveWonPage());
            System.out.println("OnWinner OUT");
        }
        else {
            //the winner is not this client, who has lost instead
            Platform.runLater(() -> showYouHaveLostPage(nickname));
            System.out.println("OnWinner OUT");
        }
    }

    /**
     * Method of the ClientObserver interface that is fired by the client when a user loses
     */

    @Override
    public void OnLoser() {
        System.out.println("OnLoser IN");
        Platform.runLater(() -> showYouHaveLostPage(""));
        System.out.println("OnLoser OUT");
    }


    /**
     * Method of the ClientObserver interface that is fired by the client when receiving the updated board when it's not playing
     *
     * @param nodes board cells updated and the playing player's nickname
     */
    @Override
    public void OnPrintUpdatedBoard(NodeList nodes) {

        Node node;
        for (int i = 0; i < nodes.getLength(); i++) {
            node = nodes.item(i);

            if (node.getNodeName().equals("board")) {
                this.nodeboard = node;
            } else if (node.getNodeName().equals("playingPlayer")) {
                this.playingPlayerNickname = node.getAttributes().getNamedItem("nickname").getTextContent();
            }
        }
        this.currentPhase = Phase.Update;
        Platform.runLater(() -> showBoardPage_UniqueBoard());
    }



    /* ******************************************************************************************************************* *
     *                                      UTILITY METHODS FOR MANAGING THE BOARD TO SHOW                                 *
     * ******************************************************************************************************************* */

    /**
     * Method that returns the node of the cell that has a certain id from the board in xml format (nodeboard of the class)
     *
     * @param cellnodeid is of the cell node that is being found
     * @return the cell node found with the given id, null if there were no cells in the board given by the server
     */

    public Node getCellNodeGivenTheID(int cellnodeid) {
        Node cell;

        if (nodeboard.hasChildNodes()) {
            NodeList cells = nodeboard.getChildNodes();
            for (int i = 0; i < cells.getLength(); i++) {
                cell = cells.item(i);

                if (cell.getNodeName().equals("cell")) {
                    if (Integer.parseInt(cell.getAttributes().getNamedItem("id").getTextContent()) == cellnodeid) {
                        return cell;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Method to get the id of a cell given its node
     *
     * @param cellnode node in xml format from which to get the id
     * @return the id of the cell
     */

    public int getIdOfCellNode(Node cellnode) {
        return Integer.parseInt(cellnode.getAttributes().getNamedItem("id").getTextContent());
    }

    /**
     * Method to get the nickname of a cell given its node
     *
     * @param cellnode node in xml format from which to get the nickname (it can be an empty string if the cell has no nikcname)
     * @return the nickname associated to the cell
     */

    public String getNicknameOfCellNode(Node cellnode) {

        return cellnode.getAttributes().getNamedItem("nickname").getTextContent();
    }

    /**
     * Method to get the level of a cell given its node
     *
     * @param cellnode node in xml format from which to get the level
     * @return the level of the cell
     */

    public int getLevelOfCellNode(Node cellnode) {
        return Integer.parseInt(cellnode.getAttributes().getNamedItem("level").getTextContent());
    }

    /**
     * Method to get the dome of a cell given its node
     *
     * @param cellnode node in xml format from which to get the dome
     * @return true fi the call has a dome, otherwise false
     */

    public boolean getDomeOfCellNode(Node cellnode) {
        return Boolean.parseBoolean(cellnode.getAttributes().getNamedItem("dome").getTextContent());
    }

    /**
     * Method that resets the candidate cells saved in the GUI
     * IMPORTANT: use this each time before sending the answer to the server
     */

    public void restoreCandidateCells() {
        indexcandidatecells.clear();
    }

    /* ******************************************************************************************************************* *
     *                                METHODS CALLED BY THE GUI CONTROLLER THAT TRIGGER ACTIONS                            *
     * ******************************************************************************************************************* */

    /**
     * Method called by GUI Controller to Connect
     * @param serverip ip of the server
     */
    public void doConnect(String serverip) {
        client.Connect(serverip);
    }

    /**
     * Method called by GUI Controller to Exit from the game
     */
    public void doExit() {
        client.Disconnect();
        Platform.exit();
        System.exit(0);
    }
    /**
     * Method called by GUI Controller to Disconnect the player
     */
    public void doDisconnect() {
        client.Disconnect();
    }
    /**
     * Method called by GUI Controller to Register the player
     * @param nickname user's nickname
     */
    public void doRegister(String nickname) {
        client.Register(nickname);
    }
    /**
     * Method called by GUI Controller to Deregister the player
     */
    public void doDeregister() {
        if(gods != null) {
            gods.clear();
        }
        if(players != null) {
            players.clear();
        }
        indexcandidatecells.clear();
        PlayerGodMap.clear();
        PlayerWorkerMap.clear();
        firstPlayersGod = "";
        deadPlayers.clear();
        playingPlayerNickname = "";
        client.Deregister();
    }
    /**
     * Method called by GUI Controller to search a match
     * @param numberOfPlayers number of players that are playing the match
     */
    public void doSearchMatch(int numberOfPlayers) {
        client.SearchMatch(numberOfPlayers);
    }

    /**
     * Method called by GUI Controller to send the chosen gods
     * @param GodsChosen the gods chosen by first player
     */
    public void doSendGods(List<String> GodsChosen){
        client.ChosenGods(GodsChosen);
        Platform.runLater(() -> showWaitingPage("Wait while the other players choose their gods"));
    }

    /**
     * Method called by GUI Controller to send the god chosen by the player to play with
     * @param GodSelected god selected by the player
     */
    public void doSendSelectedGod(String GodSelected){
        client.ChosenGod(GodSelected);
        Platform.runLater(() -> showWaitingPage("Wait for your turn to begin"));
    }

    /**
     * Method called by GUI Controller to send the first Player
     * @param FirstPlayer player that will start first the match
     */
    public void doSendFirstPlayer(String FirstPlayer){
        client.ChosenFirstPlayer(FirstPlayer);
        Platform.runLater(() -> showWaitingPage("Wait for your turn to begin"));
    }

    /**
     * Method called by GUI Controller to send the cell selected to place the workers
     * @param CellsSelected cells where to place the workers
     */
    public void doSendSelectedCellsForWorkers(List<String> CellsSelected){
        String[] chosenPosition = new String[2];
        String firstPosition = CellsSelected.get(0);
        String secondPosition = CellsSelected.get(1);
        //int indexFirstPosition = (firstPosition.charAt(0) - 'A') * 5 + (firstPosition.charAt(1) - '1');
        //int indexSecondPosition = (secondPosition.charAt(0) - 'A') * 5 + (secondPosition.charAt(1) - '1');
        //chosenPosition[0] = Integer.toString(indexFirstPosition);
        //chosenPosition[1] = Integer.toString(indexSecondPosition);

        chosenPosition[0] = CellsSelected.get(0);
        chosenPosition[1] = CellsSelected.get(1);

        client.ChosenWorkersFirstPositions(chosenPosition[0], chosenPosition[1]);
        Platform.runLater(() -> showWaitingPage("Wait for your turn to begin"));
    }

    /**
     * Method called by GUI Controller to send the worker chosen by the player to play the turn
     * @param chosenWorker worker that will move and build
     */
    public void doSendSelectedWorker(String chosenWorker){
        client.ChosenWorker(chosenWorker);
    }

    /**
     * Method called by GUI Controller to send the candidate cell for the move Phase
     * @param candidateCell cell selected by the player to move the worker on
     */
    public void doSendCandidateMove(String candidateCell){
        this.restoreCandidateCells();
        client.CandidateMove(candidateCell);
    }

    /**
     * Method called by GUI Controller to skip the extra move phase (only Artemis has an extra move phase)
     */
    public void doSkipOptMove(){
        this.restoreCandidateCells();
        client.passMove();
    }

    /**
     * Method called by GUI Controller to skip the extra build phase generated by the power of some gods
     */
    public void doSkipOptBuild(){
        this.restoreCandidateCells();
        client.passBuild();
    }

    /**
     * Method called by GUI Controller to send the candidate cell for the build Phase
     * @param candidateCell cell selected by the player to build on
     */
    public void doSendCandidateBuild(String candidateCell){
        this.restoreCandidateCells();
        client.CandidateBuild(candidateCell);
    }

    /**
     * Method called by GUI Controller to send the candidate cell for the end Phase
     * @param candidateCell cell selected by the player to do the end phase on
     */
    public void doSendCandidateEnd(String candidateCell){
        this.restoreCandidateCells();
        client.CandidateEnd(candidateCell);
    }

    /**
     * Method called by GUI Controller to skip the end phase generated by the power of some gods
     */
    public void doSkipOptEnd(){
        this.restoreCandidateCells();
        client.passEnd();
    }

    /**
     * Method called by GUI Controller to send the candidate cell for the build Phase (Atlas only)
     * @param candidateCell cell selected by the player to build on
     * @param LevelOrDome indicate if the player wants to build a dome or a standard level
     */
    public void doSendCandidateBuildForAtlas(String candidateCell, String LevelOrDome) {
        this.restoreCandidateCells();
        client.CandidateBuildForAtlas(candidateCell, LevelOrDome);
    }

    /**
     * Method called by GUI Controller to start a new match, skipping the registration process
     */
    public void doPlayAgain() {
        if(gods != null) {
            gods.clear();
        }
        if(players != null) {
            players.clear();
        }
        indexcandidatecells.clear();
        PlayerGodMap.clear();
        PlayerWorkerMap.clear();
        firstPlayersGod = "";
        deadPlayers.clear();
        playingPlayerNickname = "";
        Platform.runLater(() -> showRegisteredPage());
    }

    /**
     * Method called by GUI Controller to get the current Phase of the turn
     * @return the current phase of the turn
     */
    public Phase getCurrentPhase(){
        return this.currentPhase;
    }
}


