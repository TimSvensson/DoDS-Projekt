
/**
 * Created by axelhellman on 2016-12-08.
 */
public class Board {

    Player [] listOfPlayers;
    Square[] listOfSquares;
    int totalPlayer = 0;
    Deck deck_1;
    Deck deck_2;
    Deck deck_3;
    int currentPlayer = 0;

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

    public Square movePlayer(Player player, Dice dice){
        player.incrementPosition(player.tossDie(dice));
        // Om spelare hamnar i fängelset, så fixar vi kod till det sen

        int currentPlayerPosition = player.getPosition();

        Square toReturn = null;

        for (int i = 0; i < listOfSquares.length; i++) {
            if(listOfSquares[i].getPosition() == currentPlayerPosition) {
                toReturn = listOfSquares[i];
                break;
            }
        }

        /*for (Square x = listOfSquares.getFirst(); x != null; x = x.getNext()) {
            if (x.getPosition() == currentPlayerPosition) {
                toReturn = x;
                break;
            }
        }*/

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