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
/*
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

        int rectWidth = 35;
        int rectHeight = 35;
        int widthHalf = rectWidth / 2;
        int heightHalf = rectHeight / 2;

        int j = 1;
        for (int i = 1; i < 11; i++) {
            Square r1 = new Square(rectWidth, heightHalf);
            Square r2 = new Square(rectWidth, heightHalf);
            Square r3 = new Square(widthHalf, rectHeight);
            Square r4 = new Square(widthHalf, rectHeight);

            r1.fillDefaults();
            r2.fillDefaults();
            r3.fillDefaults();
            r4.fillDefaults();

            grid.add(r1, 0, j);
            grid.add(r2, 11, j);
            grid.add(r3, i, 0);
            grid.add(r4, i, 12);
            j++;
        }
        for (int i = 0; i < 12; i++) {
            if (i == 0 || i == 11) {
                Square r1 = new Square(rectWidth, rectHeight);
                Square r2 = new Square(rectWidth, rectHeight);
                r1.fillDefaults();
                r2.fillDefaults();
                grid.add(r1, i, 0);
                grid.add(r2, i, 12);
            }
        }


        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);

        /*GridPane grid2 = new GridPane();
        grid2.setGridLinesVisible(true);
        grid2.setAlignment(Pos.BOTTOM_CENTER);
        grid2.setHgap(5);
        grid2.setVgap(5);
        grid2.setPadding(new Insets(15, 15, 15, 15));


        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid2.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User name:");
        grid2.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid2.add(userTextField, 1, 1);

        Label pw = new Label("Password: ");
        grid2.add(pw, 1, 1);

        PasswordField pwBox = new PasswordField();
        grid2.add(pwBox, 1, 1);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid2.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid2.add(actiontarget, 1, 6);

        btn.setOnAction(event -> {
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText(userTextField.getText());

        });*/
/*
        primaryStage.show();

    }


}
*/