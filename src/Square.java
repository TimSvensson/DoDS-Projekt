import com.alibaba.fastjson.*t;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by axelhellman on 2016-12-08.
 */
public class Square extends Rectangle{
    Square next;
    String name;
    int position;

    public Square(){

    }

    public Square(int width, int height) {
        super (width, height);
    }

    public Square(String name, int position)
    {
        this.name = name;
        this.position = position;
    }

    protected void fillDefaults() {
        this.setFill(Color.TRANSPARENT);
        this.setStroke(Color.BLACK);
        this.setStrokeWidth(3);
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

    // TODO!!!! Kolla över hur newNext ska genereras.
    /*public Square recreateSquare(JSONObject toRecreate) {
        String newName = (String) toRecreate.get("name");
        int newPosition = (Integer) toRecreate.get("position");
        JSONArray jsonNewNext = toRecreate.getJSONArray("next");


        Square toReturn = new Square(newName, newPosition);
        toReturn.setNext(newNext);

        return toReturn;
    }*/
}


