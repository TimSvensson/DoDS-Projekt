
/**
 * Created by axelhellman on 2016-12-08.
 */
public class Board {

    Player [] listOfPlayer;
    List_t listOfSquares;
    int totalPlayer = 0;
    Deck deck_1;
    Deck deck_2;
    Deck deck_3;
    int currentPlayer = 0;

    public Board(int totalPlayer) {
        listOfPlayer = new Player[totalPlayer];
        this.totalPlayer = totalPlayer;
        for (int i = 0; i < listOfPlayer.length; i++) {
            listOfPlayer[i] = new Player(i, "Player " + (i + 1));
        }
    }

    public  Square movePlayer(Player player, int die1, int die2){
        player.setPosition(die1+die2);
        // Om spelare hamnar i fängelset, så fixar vi kod till det sen

        int currentPlayerPosition = player.getPosition();

        Square toReturn = null;

        for (Square x = listOfSquares.getFirst(); x != null; x = x.getNext()) {
            if (x.getPosition() == currentPlayerPosition) {
                toReturn = x;
                break;
            }
        }

        return toReturn;
    }

    public Player[] getListOfPlayer() {
        return listOfPlayer;
    }

    public Player getCurrentPlayer() {
        return listOfPlayer[currentPlayer];
    }

    public void nextTurn() {
        if(currentPlayer++ == listOfPlayer.length){
            currentPlayer = 0;
        }
    }

    public int getTotalPlayer() {
        return totalPlayer;
    }
    public Player getPlayerByID(int id){
        Player toReturn = null;
        for (Player p : listOfPlayer) {
            if (p.getID() == id) {
                toReturn = p;
            }
        }

        return toReturn;
    }

    public List_t getListOfSquares() {
        return listOfSquares;
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


public int getTotalSquare() {
        return squares.length;
        }

        }*/