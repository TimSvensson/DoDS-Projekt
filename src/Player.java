/**
 * Created by axelhellman on 2016-12-08.
 */
public class Player {
    int id;
    int position = 0;
    int totalwalk = 0;
    Money money = new Money(10); //haha u poor bitch?

    public Player (int id){
        this.id = id; // Ska vi ha ett namn?????????????????????
    }
    public int getTotalWalk(){
        return totalwalk;
    }

    public int getPosition(){
        return position;
    }
    public void setPosition(int poistion){
        this.position = position;
    }
    public int getID(){
        return id;
    }
    public int TossDie(Dice die){
        int die1 = die.getDie1();
        int die2 = die.getDie2();
        System.out.println(getID() + "tossed die 1 and got:" + die1 + "tossed die2 and got:"+ die2);
        return (die1 + die2);
    }
}


