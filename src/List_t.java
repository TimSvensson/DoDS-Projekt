/**
 * Created by axelhellman on 2016-12-08.
 */
/*public class List_t {
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

    public void setFirst(Square first) {
        this.first = first;
    }

    public void setLast(Square last) {
        this.last = last;
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
            this.length++;
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

    /*public void deleteList() {
        Square s = this.getFirst();

        for (int i = 0; i < this.length; i++) {
            Square n = s.getNext();
            this.setFirst(null);
            s = n;
        }
    }

    public List_t recreateList(JSONObject toRecreate) {
        int newLength = (Integer) toRecreate.get("length");

        while ()
        Square newFirst = recreateSquare((Square) toRecreate.get("first"));
        Square newLast = recreateSquare((Square) toRecreate.get("last"));


    }
}
*/

