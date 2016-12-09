/**
 * Created by axelhellman on 2016-12-08.
 */
public class Player {
    int id;
    int position = 0;
    int totalwalk = 0;
    String name;
    //String ip; Kanske ska länka detta i monopolet istället så att id:et länkar till en spelares IP
    Money money = new Money(5000); //haha u poor bitch?


    public Player(int id, String name) {
        this.id = id;
        this.name = name;
    }


    public int getTotalWalk(){
        return totalwalk;
    }


   /* *public String getIp() {
        return ip;
    }*/

    public int getPosition(){
        return position;
    }
    public void setPosition(int position){
        this.position = position;
    }
    public int getID(){
        return id;
    }
    public String getName(){
        return name;
    }

    public int TossDie(Dice die){
        int die1 = die.getDie1();
        int die2 = die.getDie2();
        System.out.println(getID() + "tossed die 1 and got:" + die1 + "tossed die2 and got:"+ die2);
        return (die1 + die2);
    }
}


