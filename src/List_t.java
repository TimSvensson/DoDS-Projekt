/**
 * Created by axelhellman on 2016-12-08.
 */
public class List_t {
    Square first;
    Square last;
    int length; // = 40;


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

    public Square getFirst() {
        return first;
    }

    public Square getLast() {
        return last;
    }

    // OBS!!! Haubir och Axel har föreslagit dessa metoder och konstruktor(er) till vår länkade lista
    public List_t(String [] listOfNames) {
        this.first = null;
        this.last = null;
        fillList(listOfNames);
    }

    public void fillList(String [] listOfNames) {
        int desiredLength = 5; // Den ska vara 40 när vi är klara, men framtills dess är den det vi vill att den ska vara

        for (int i = 0; i < desiredLength; i++) {
            Square s = new Square(listOfNames[i], i);
            fillListAux(s);
        }

    }

    public void fillListAux(Square toInsert) {
        if (this.first == null) {
            this.first = this.last = toInsert;
        }
        else {
            this.last.setNext(toInsert);
            this.last = this.last.getNext();
        }
    }
}


