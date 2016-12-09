/**
 * Created by axelhellman on 2016-12-08.
 */
public class Square {
    Square next;
    String name;
    int position;

    public Square(){

    }

    public Square(String name, int position)
    {
        this.name = name;
        this.position = position;
    }


    public String getName()
    {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public Square getNext() {
        return next;
    }

    public void setNext(Square next) {
        this.next = next;
    }
}


