/**
 * Created by axelhellman on 2016-12-08.
 */
public class List_t {
    Square first;


    public List_t() {
        int ack = 15;
        this.first = new Square();
        this.first = insert(first, ack);
    }

    private Square insert(Square f, int ack) {
        if (ack > 0) {
            f.next = new Square();
            ack--;
            insert(f.next, ack);
        }
        return f;
    }
}

