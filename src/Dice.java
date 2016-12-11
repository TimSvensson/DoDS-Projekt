package itmonopol;

/**
 * Created by axelhellman on 2016-12-08.
 */
/**
 *
 * @author axelhellman
 */
protected class Dice {

    protected int die1;   // Number showing on the first die.
    protected int die2;



    protected Dice() {
        // Constructor.  Rolls the dice, so that they initially
        // show some random values.
        roll();  // Call the roll() method to roll the dice.
    }

    protected void roll() {
        // Roll the dice by setting each of the dice to be
        // a random number between 1 and 6.
        //die1 = (int)(Math.random()*6) + 1;
        //die2 = (int)(Math.random()*6) + 1;
        die1 = 0; // Tillfälligt. Ta bort denna kommentar när den inte längre behövs
        die2 = 1;
    }

    protected int getDie1() {
        // Return the number showing on the first die.
        return die1;
    }
    protected int getDie2() {
        return die2; //Return the number of die 2
    }

    protected int getTotal()
    {
        return die1 + die2;
    }
}
