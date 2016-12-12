import com.alibaba.fastjson.*;

/**
 * Created by axelhellman on 2016-12-08.
 */
public class Money {
    int amount;

    public Money(){
        this(0);
    }
    public Money (int amount){
        this.amount = amount;
    }

    public int getMoney() {
        return amount;
    }
    public void addMoney(int amount){
        this.amount += amount;
    }
    public void removeMoney(int amount){
        this.amount -= amount;
    }
    public boolean isBroke(){
        return amount <= 0;
    }

    /*public Money recreateMoney(JSONObject toRecreate) {
        int newAmount = (Integer) toRecreate.get("amount");
        Money toReturn = new Money(newAmount);

        return toReturn;
    }*/
}


