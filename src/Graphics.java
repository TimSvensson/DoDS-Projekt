import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import javafx.application.Application;
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


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Monopol!");
        primaryStage.setFullScreen(false);
        GridPane grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        double rectWidth = 35;
        double rectHeight = 35;
        double widthHalf = rectWidth / 2;
        double heightHalf = rectHeight / 2;

        int j = 1;
        for (int i = 1; i < 11; i++) {
            Rectangle r1 = new Rectangle(rectWidth, heightHalf);
            Rectangle r2 = new Rectangle(rectWidth, heightHalf);
            Rectangle r3 = new Rectangle(widthHalf, rectHeight);
            Rectangle r4 = new Rectangle(widthHalf, rectHeight);

            fillDefaults(r1);
            fillDefaults(r2);
            fillDefaults(r3);
            fillDefaults(r4);

            grid.add(r1, 0, j);
            grid.add(r2, 11, j);
            grid.add(r3, i, 0);
            grid.add(r4, i, 12);
            j++;
        }
        for (int i = 0; i < 12; i++) {
            if (i == 0 || i == 11) {
                Rectangle r1 = new Rectangle(rectWidth, rectHeight);
                Rectangle r2 = new Rectangle(rectWidth, rectHeight);
                fillDefaults(r1);
                fillDefaults(r2);
                grid.add(r1, i, 0);
                grid.add(r2, i, 12);
            }
        }


        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);

        primaryStage.show();

    }
    protected void fillDefaults(Rectangle r) {
        r.setFill(Color.TRANSPARENT);
        r.setStroke(Color.BLACK);
        r.setStrokeWidth(3);
    }

}
