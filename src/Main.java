/**
 * Created by Haubir on 12/15/16.
 */
public class Main {
    public static void main(String[] args) {
        // Skapa nytt graphics objekt och tryck upp på skärmen
        // Fyll det med data och knappar
        Graphics window = new Graphics();
        Graphics.main(args);

        window.drawPlayer(window.grid);
        /*GameLoop g = new GameLoop();
        new Thread(g).start();*/
    }

    protected void run() {

    }
}
