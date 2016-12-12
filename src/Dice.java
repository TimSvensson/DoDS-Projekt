/**
 * Created by axelhellman on 2016-12-08.
 */

/**
 *
 * @author axelhellman
 */
public class Dice {

    protected int die1;   // Number showing on the first die.
    protected int die2;

    public Dice(int die1, int die2) {
        this.die1 = die1;
        this.die2 = die2;
    }

    public Dice() {
        // Constructor.  Rolls the dice, so that they initially
        // show some random values.
        roll();  // Call the roll() method to roll the dice.
    }

    public void roll() {
        // Roll the dice by setting each of the dice to be
        // a random number between 1 and 6.
        //die1 = (int)(Math.random()*6) + 1;
        //die2 = (int)(Math.random()*6) + 1;
        die1 = 0; // Tillfälligt. Ta bort denna kommentar när den inte längre behövs
        die2 = 1;
    }

    public int getDie1() {
        // Return the number showing on the first die.
        return die1;
    }
    public int getDie2() {
        return die2; //Return the number of die 2
    }

    public int getTotal()
    {
        return die1 + die2;
    }

    /*public Dice recreateDice(JSONObject toRecreate) {
        int newDie1 = (Integer) toRecreate.get("die1");
        int newDie2 = (Integer) toRecreate.get("die2");

        Dice toReturn = new Dice(newDie1, newDie2);

        return toReturn;
    } */
}
