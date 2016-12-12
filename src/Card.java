import com.alibaba.fastjson.*;

/**
 * Created by axelhellman on 2016-12-09.
 */

public class Card {
    private String descCard;

    public Card(String descCard) {
        this.descCard = descCard;
    }

    public String getDescCard() {
        return descCard;
    }

    public Card recreateCard(JSONObject toRecreate) {
        String newDescCard = (String) toRecreate.get("descCard");
        Card toReturn = new Card(newDescCard);

        return toReturn;
    }
}
