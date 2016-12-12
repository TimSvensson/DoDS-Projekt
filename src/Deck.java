import com.alibaba.fastjson.*;



/**
 * Created by axelhellman on 2016-12-09.
 */
public class Deck {
    Card[] cardArray;

    public Deck(Card[] cardarray) {
        this.cardarray = cardarray;
    }

    public Card[] getCardarray() {
        return cardarray;
    }

    // recreateCard() finns i Card-klassen, skumt nog hittar inte Deck-klassen den just nu.
    /*public Deck recreateDeck(JSONObject toRecreate) {
        JSONArray newCardArrayJSON = toRecreate.getJSONArray("cardArray");
        Card[] newCardArray = new Card[10];

        for (int i = 0; i < newCardArrayJSON.size(); i++) {
            JSONObject newCard = (JSONObject) newCardArrayJSON.get(i);
            Card c = recreateCard(newCard);
            newCardArray[i] = c;
        }

        Deck toReturn = new Deck(newCardArray);

        return toReturn;

    } */
}
