package itmonopol;

/**
 * Created by axelhellman on 2016-12-08.
 */
protected class Money {
    int money;

    protected Money(){
        this(0);
    }
    protected Money (int Money){
        this.money = money;
    }

    protected int getMoney() {
        return money;
    }
    protected void addMoney(int amount){
        money += amount;
    }
    protected void removeMoney(int amount){
        money -= amount;
    }
    protected boolean isBroke(){
        return money <= 0;
    }
}


