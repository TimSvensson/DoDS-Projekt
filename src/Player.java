/**
 * Created by axelhellman on 2016-12-08.
 */
public class Player {
    protected int id;
    protected int prevPosition = 0;
    protected int position = 0;
    protected int totalwalk = 0;
    protected int turn;
    String name;
    Money money = new Money(5000); //haha u poor bitch?

    public Player(int turn, String name, int id) {
        this.turn = turn;
        this.name = name;
        this.id = id;
    }

    public int getTotalWalk(){
        return totalwalk;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getPrevPosition() {
        
        return prevPosition;
    }

    public int getPosition(){
        return this.position;
    }

    public void setPosition(int position){
        this.position = position;
    }

    protected void incrementPosition(int p) {
        this.position += p;
    }

    public String getName(){
        return name;
    }

    public int tossDie(Dice die){
        die.roll();
        int die1 = die.getDie1();
        int die2 = die.getDie2();
        System.out.println("Player " + getId() + " tossed die 1 and got: " + die1 + "\nPlayer " + getId() + " tossed die 2 and got: "+ die2);
        return (die1 + die2);
    }
}


