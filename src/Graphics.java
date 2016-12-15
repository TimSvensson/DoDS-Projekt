import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.reflect.Field;

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
            System.out.println("Usage: $ java Graphics <IS_SERVER> <serverName> <serverPort>");
        }

        //Monopoly.main(args);
        launch(args);

        System.exit(0);
    }

    private GridPane addGrid(){
        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        setBoard();
        return grid;
    }



    private Button addHBox() {

        /*HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
*/
        Button btnDice = new Button("Throw dice");
        btnDice.setPrefSize(100, 20);

       // hbox.getChildren().addAll(btnDice, field2);

        return btnDice;
    }

    private Text addTextField(){

        Text field = new Text();
        return field;
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Monopol!");
        primaryStage.setFullScreen(false);

        BorderPane border = new BorderPane();
        border.setCenter(addGrid());



        border.setTop(addHBox());
        border.setBottom(addTextField());

        Text actiontarget = (Text) border.getChildren().get(2);

        Button nodeOut = (Button) border.getChildren().get(1);
        
        nodeOut.setOnAction(e -> {
            actiontarget.setText("Moved player");
        });

        Scene scene = new Scene(border, 800, 800);
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
