package itmonopol;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by axelhellman on 2016-12-08.
 */
protected class Square extends Rectangle{
    Square next;
    String name;
    int position;

    protected Square(){

    }

    protected Square(int width, int height) {
        super (width, height);
    }

    protected Square(String name, int position)
    {
        this.name = name;
        this.position = position;
    }

    protected void fillDefaults() {
        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
    }

    protected String getName()
    {
        return name;
    }

    protected int getPosition() {
        return position;
    }

    protected Square getNext() {
        return next;
    }

    protected void setNext(Square next) {
        this.next = next;
    }
}


