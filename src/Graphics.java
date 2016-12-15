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
    Tuple[] pos = new Tuple[23];
    GridPane grid = new GridPane();

    public Graphics() {

    }


    public static void main(String[] args) {
        Monopoly.main(args);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Monopol!");
        primaryStage.setFullScreen(false);

        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        setBoard(grid);

        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        Player kuksugare = new Player(1, "Fittan");
        kuksugare.position = 5;
        kuksugare.prevPosition = 0;
        drawPlayer(kuksugare, grid, pos);

    }
    protected void fillDefaults(Rectangle r) {
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.BLACK);
        r.setStrokeWidth(3);
    }



    protected void setBoard(GridPane g) {
        double rectWidth = 35;
        double rectHeight = 35;
        int squareID = 0;
        for (int i = 11; i >= 0; i--) {
            Rectangle r = new Rectangle(rectWidth, rectHeight);
            fillDefaults(r);
            r.setId(Integer.toString(squareID));
            g.add(r, i, 11);
            squareID++;
        }

        for (int i = 11; i >= 0; i--) {
            Rectangle r = new Rectangle(rectWidth, rectHeight);
            fillDefaults(r);
            r.setId(Integer.toString(squareID));
            g.add(r, 0, i);
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

    }

    protected void drawPlayer(Player p, GridPane grid, Tuple[] pos) {
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
