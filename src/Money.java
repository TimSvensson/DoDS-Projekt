/**
 * Created by axelhellman on 2016-12-08.
 */
public class Money {
    int money;

    public Money(){
        this(0);
    }
    public Money (int Money){
        this.money = money;
    }

    public int getMoney() {
        return money;
    }
    public void addMoney(int amount){
        money += amount;
    }
    public void removeMoney(int amount){
        money -= amount;
    }
    public boolean isBroke(){
        return money <= 0;
    }
}


