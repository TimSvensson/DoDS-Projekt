package itmonopol;
/**
 * Created by axelhellman on 2016-12-08.
 */
protected class Board {

    Player [] listOfPlayer;
    List_t listOfSquares;
    int totalPlayer = 0;
    Deck deck_1;
    Deck deck_2;
    Deck deck_3;
    int currentPlayer = 0;

    protected Board(int totalPlayer) {
        listOfPlayer = new Player[totalPlayer];
        this.totalPlayer = totalPlayer;
        for (int i = 0; i < listOfPlayer.length; i++) {
            listOfPlayer[i] = new Player(i, "Player " + (i + 1));
        }
    }

    protected Square movePlayer(Player player, Dice dice){
        player.setPosition(player.tossDie(dice));
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

    protected Player[] getListOfPlayer() {
        return listOfPlayer;
    }

    protected Player getCurrentPlayer() {
        return listOfPlayer[currentPlayer];
    }

    protected void nextTurn() {
        if(currentPlayer++ == listOfPlayer.length){
            currentPlayer = 0;
        }
    }

    protected int getTotalPlayer() {
        return totalPlayer;
    }
    protected Player getPlayerByID(int id){
        Player toReturn = null;
        for (Player p : listOfPlayer) {
            if (p.getID() == id) {
                toReturn = p;
            }
        }

        return toReturn;
    }

    protected List_t getListOfSquares() {
        return listOfSquares;
    }

    protected void setListOfSquares(List_t listOfSquares) {
        this.listOfSquares = listOfSquares;
    }

    // TODO (KANSKE) kan vara bra att hålla koll på antalet squares.
}
/**



 protected boolean hasWinner() {
        int ingame = 0;
        for(Player player:players){
        if(!player.isBrokeOut()){
        ingame++;
        }
        }
        return ingame <= 1;
        }

protected Player getWinner() {
        if(!hasWinner()){ return null; }
        for(Player player:players){
        if(!player.isBrokeOut()){ return player; }
        }
        return null;
        }

protected Player getMaxMoneyPlayer() {
        Player maxplayer = null;
        for(Player player:players){
        if(maxplayer == null || maxplayer.getMoney().getMoney() < player.getMoney().getMoney()){
        maxplayer = player;
        }
        }
        return maxplayer;
        }

protected int normalizePosition(int position) {
        return position % squares.length;
        }


protected int getTotalSquare() {
        return squares.length;
        }

        }*/