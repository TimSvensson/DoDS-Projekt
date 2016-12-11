package itmonopol;
/**
 * Created by axelhellman on 2016-12-09.
 */
protected class Deck {
    Card[] cardarray;

    protected Deck(Card[] cardarray) {
        this.cardarray = cardarray;
    }

    protected Card[] getCardarray() {
        return cardarray;
    }
}
