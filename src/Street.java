package itmonopol;

/**
 * Created by axelhellman on 2016-12-09.
 */
protected class Street extends Square {
    String streetName;
    int price;
    int buildLevel;
    Player owner;

    protected Street(String streetName) {
        this.streetName = streetName;
        this.price = price;
    }

    protected String getStreetName() {
        return streetName;
    }

    protected int getPrice(){
        return price;
    }

    protected Player getOwner() {
        return owner;
    }

    protected int getBuildLevel() {
        return buildLevel;
    }
    // TODO lite allt möjligt typ sätta ägare på en gata/square etc...
}
