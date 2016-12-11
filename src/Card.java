package itmonopol;
/**
 * Created by axelhellman on 2016-12-09.
 */

protected class Card {
    String desc_card;

    protected Card(String desc_card) {
        this.desc_card = desc_card;
    }

    protected String getDesc_card() {
        return desc_card;
    }
}
