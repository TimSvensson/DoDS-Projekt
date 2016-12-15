import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Graphics extends Application {

    // Tims flaggor, variabler och annat roligt
    public static boolean IS_SERVER;
    public static String serverName;
    public static int serverPort;


    Tuple[] pos = new Tuple[23];
    GridPane grid = new GridPane();

    public Graphics() {

    }


    public static void main(String[] args) {

        if (args.length == 0) {
            IS_SERVER = true;
            serverName = "localhost";
            serverPort = 9000;
        } else if (args.length == 3) {

            if (args[0].equals("server")) {
                IS_SERVER = true;
            } else {
                IS_SERVER = false;
            }

            serverName = args[1];
            serverPort = Integer.parseInt(args[2]);

        } else {
            System.out.println("Usage: <IS_SERVER> <serverName> <serverPort>");
        }

        //Monopoly.main(args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Monopol!");
        primaryStage.setFullScreen(false);
        grid = setBoard();
        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        setBoard();

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 0, 12);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 12);

        Player kuksugare = new Player(1, "Fittan");

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

                kuksugare.prevPosition = kuksugare.position;
                kuksugare.incrementPosition(kuksugare.tossDie(new Dice()));
                drawPlayer(kuksugare, pos);
                actiontarget.setText("Moved player");
            }
        });

        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();





    }
    protected void fillDefaults(Rectangle r) {
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.BLACK);
        r.setStrokeWidth(3);
    }



    protected GridPane setBoard() {
        double rectWidth = 35;
        double rectHeight = 35;
        int squareID = 0;
        for (int i = 11; i >= 0; i--) {
            Rectangle r = new Rectangle(rectWidth, rectHeight);
            fillDefaults(r);
            r.setId(Integer.toString(squareID));
            grid.add(r, i, 11);
            squareID++;
        }

        for (int i = 11; i >= 0; i--) {
            Rectangle r = new Rectangle(rectWidth, rectHeight);
            fillDefaults(r);
            r.setId(Integer.toString(squareID));
            grid.add(r, 0, i);
            squareID++;
        }

        int columnIndex = 11;
        int rowIndex = 11;
        int ack = 0;
        for (int i = 0; i < 23; i++) {
            pos[i] = new Tuple(i, columnIndex, rowIndex);
            if(columnIndex >= 1) columnIndex--;
            if (ack >= 11) rowIndex--;
            ack++;
         }
    return grid;
    }

    protected void drawPlayer(Player p, Tuple[] pos) {
        p.prevPosition = p.position;
        for (int i = 0; i < 23; i++) {
            if (p.position == pos[i].Position) {
                Circle c = new Circle(15);
                grid.add(c, pos[i].first, pos[i].second);
            }
        }
    }

    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }


}
