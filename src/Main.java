/**
 * Created by Haubir on 12/15/16.
 */
public class Main {
    public static void main(String[] args) {
        // Skapa nytt graphics objekt och tryck upp på skärmen
        // Fyll det med data och knappar

        boolean isServer;

        if ("server".equals(args[0].toLowerCase())) {
            isServer = true;
        }
        else {
            isServer = false;
        }
        GameLoop game = new GameLoop();
        game.run(isServer);
    }
}
