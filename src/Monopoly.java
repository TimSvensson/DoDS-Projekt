package itmonopol;
/**
 * Created by axelhellman on 2016-12-08.
 */

////String ip (Player); Kanske ska länka detta i monopolet istället så att id:et länkar till en spelares IP
protected class Monopoly {
        Dice dice = new Dice();
        Board board;
        // String [] list_of_ips;
        // String [] list_of_playerID; // Länkar ihop spelarnas id:n med ip:addresserna

        protected Monopoly(int totalPlayer) {
                board = new Board(totalPlayer);
        }

        protected Dice getDice() {
                return dice;
        }

        protected Board getBoard() {
                return board;
        }



        protected static void main(String[] args) {
        //TODO Gör en while med scanner som bestämmer hur många spelar vi ska ha med i spelet.
                int numberOfPlayers = 1; // Ändra den till vad du vill Adam
                Monopoly game = new Monopoly(numberOfPlayers); // Nytt spel skapas

                Board b = game.getBoard(); // Tillgång till spelbrädet
                Dice d = game.getDice(); // Tillgång till speltärningarna

                String[] names = {"a", "b", "c", "d", "e"}; // Namn till rutorna

                List_t l = new List_t(names); // Du har nu en länkad lista med 5 Squares, vsg. Why not liksom

                b.setListOfSquares(l); // Din länkade lista tillhör nu ditt bräde.

                Square currentSquare = b.movePlayer(b.listOfPlayer[0], d); // Kolla, vi flyttade till och med en spelare åt dig.


        }

    }


