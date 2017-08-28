import java.util.ArrayList;
import java.util.List;

/**
 * Created by axelhellman on 2016-12-08.
 */
public class Board {
    private Dice dice = new Dice();
    private List<Player> listOfPlayers = new ArrayList<>();
    String [] nameOfSquares = {
            "Street 1", "Street 2", "Street 3", "Street 4", "Street 5", "Street 6", "Street 7", "Street 8","Street 9", "Street 10",
            "Street 11", "Street 12", "Street 13", "Street 14", "Street 15", "Street 16", "Street 17", "Street 18","Street 19", "Street 20",
            "Street 21", "Street 22", "Street 23", "Street 24", "Street 25", "Street 26", "Street 27", "Street 28","Street 29", "Street 30",
            "Street 31", "Street 32", "Street 33", "Street 34", "Street 35", "Street 36", "Street 37", "Street 38","Street 39", "Street 40"}; // silvertejpskod. Fråga inte.
    private int totalPlayer = 0;
    private Deck deck_1;
    private Deck deck_2;
    private Deck deck_3;
    private int currentTurn = 0;
    private int previousTurn = 0;

    private String currentPlayerIP;
    private int currentPlayerId;

    private Square [] listOfSquares = new Square[nameOfSquares.length];
    private Square currentSquare;
    private Square previousSquare;

    public Board(List<Player> listOfPlayers) {
        this.listOfPlayers = listOfPlayers;
        totalPlayer = listOfPlayers.size();

        for (int i = 0; i < nameOfSquares.length; i++) {
            listOfSquares[i] = new Square(nameOfSquares[i], i);
        }

        currentSquare = previousSquare = listOfSquares[0];
    }

    public void movePlayer(){
        Player player = getCurrentPlayer();

        player.prevPosition = player.getPosition();
        player.incrementPosition(player.tossDie(dice));

        nextTurn();

        // Om spelare hamnar i fängelset, så fixar vi kod till det sen

        int currentPlayerPosition = player.getPosition();

        updateSquares(currentPlayerPosition);
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public int getPreviousTurn() {
        return previousTurn;
    }

    public void setPreviousTurn(int previousTurn) {
        this.previousTurn = previousTurn;
    }

    public void setCurrentPlayerIP(String currentPlayerIP) {
        this.currentPlayerIP = currentPlayerIP;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public String getCurrentPlayerIP() {
        return this.currentPlayerIP;
    }

    public int getCurrentPlayerId() {
        return this.currentPlayerId;
    }

    public Square getCurrentSquare() {
        return currentSquare;
    }

    public Square getPreviousSquare() {
        return previousSquare;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentTurn = currentPlayer;
    }

    public Player getPreviousPlayer() {
        return listOfPlayers.get(previousTurn);
    }

    public void updateSquares(int currentPlayerPosition) {
        previousSquare = currentSquare;

        for (Square square : listOfSquares) {
            if (square.getPosition() == currentPlayerPosition) {
                currentSquare = square; // TODO Va? Haubir snälla förklara. -Tim
                break;
            }
        }
    }

    public List<Player> getListOfPlayers() {
        return listOfPlayers;
    }

    public Player getCurrentPlayer() {
        return listOfPlayers.get(currentTurn);
    }
    
    public Square getPreviouseSquareOfPlayer(int player) {
        return listOfSquares[listOfPlayers.get(player).getPrevPosition()];
    }
    
    public Square getCurrentSquareOfPlayer(int player) {
        return listOfSquares[listOfPlayers.get(player).getPosition()];
    }

    public void nextTurn() {
        previousTurn = currentTurn;
        currentTurn = (currentTurn + 1) % totalPlayer;
    }

    public Dice getDice() {
        return dice;
    }

    public boolean wasDoubleDice() {
        return (dice.getDie1() == dice.getDie2());
    }

    public int getTotalPlayer() {
        return totalPlayer;
    }

    public Player getPlayerByID(int id){
        Player toReturn = null;
        for (Player p : listOfPlayers) {
            if (p.getId() == id) {
                toReturn = p;
            }
        }

        return toReturn;
    }

    public Square[] getListOfSquares() {
        return listOfSquares;
    }

    public void setListOfSquares(Square[] listOfSquares) {
        this.listOfSquares = listOfSquares;
    }

    public void deleteListOfSquares() {
        //this.listOfSquares.deleteList();
        this.listOfSquares = null;
    }

    // TODO (KANSKE) kan vara bra att hålla koll på antalet squares.
}
/**



 public boolean hasWinner() {
        int ingame = 0;
        for(Player player:players){
        if(!player.isBrokeOut()){
        ingame++;
        }
        }
        return ingame <= 1;
        }

public Player getWinner() {
        if(!hasWinner()){ return null; }
        for(Player player:players){
        if(!player.isBrokeOut()){ return player; }
        }
        return null;
        }

public Player getMaxMoneyPlayer() {
        Player maxplayer = null;
        for(Player player:players){
        if(maxplayer == null || maxplayer.getMoney().getMoney() < player.getMoney().getMoney()){
        maxplayer = player;
        }
        }
        return maxplayer;
        }

public int normalizePosition(int position) {
        return position % squares.length;
        }

        }*/