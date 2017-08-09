
/**
 * Created by axelhellman on 2016-12-08.
 */
public class Board {
    private Dice dice = new Dice();
    private Player [] listOfPlayers;
    private Square[] listOfSquares;
    private int totalPlayer = 0;
    private Deck deck_1;
    private Deck deck_2;
    private Deck deck_3;
    private int currentPlayer = 0;
    private int previousplayer = 3;

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public int getPreviousplayer() {
        return previousplayer;
    }


    public Board(int totalPlayer, String[] names) {
        listOfPlayers = new Player[totalPlayer];
        listOfSquares = new Square[names.length];
        this.totalPlayer = totalPlayer;
        for (int i = 0; i < listOfPlayers.length; i++) {
            listOfPlayers[i] = new Player(i, "Player " + (i + 1));
        }
        for (int i = 0; i < names.length; i++) {
            listOfSquares[i] = new Square(names[i], 0);
        }
    }

    public void movePlayer() {
        movePlayer(getCurrentPlayer());
    }

    public Square movePlayer(Player player){

        player.prevPosition = player.getPosition();
        player.incrementPosition(player.tossDie(dice));

        previousplayer = currentPlayer;
        if (currentPlayer < 3) currentPlayer++;
        else currentPlayer = 0;

        // Om spelare hamnar i fängelset, så fixar vi kod till det sen

        int currentPlayerPosition = player.getPosition();

        Square toReturn = null;

        for (Square listOfSquare : listOfSquares) {
            if (listOfSquare.getPosition() == currentPlayerPosition) {
                toReturn = listOfSquare;
                break;
            }
        }

        return toReturn;
    }

    public Player[] getListOfPlayers() {
        return listOfPlayers;
    }

    public Player getCurrentPlayer() {
        return listOfPlayers[currentPlayer];
    }

    public void nextTurn() {
        if(currentPlayer++ == listOfPlayers.length){
            currentPlayer = 0;
        }
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
            if (p.getID() == id) {
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

    /*public void deleteListOfSquares() {
        this.listOfSquares.deleteList();
        this.listOfSquares = null;
    }*/

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


public int getTotalSquare() {
        return squares.length;
        }

        }*/