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
        primaryStage.setTitle("JavaFX Welcome");
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

        for (int i = 0; i < 11; i++) {
            if( 0 < i && i < 10) {
                Rectangle r = new Rectangle(widthHalf, rectHeight);
                grid.add(r, i, 0);
            }
            else {
                Rectangle r = new Rectangle(rectWidth, rectHeight);
                grid.add(r, i, 0);
            }
        }
        for (int i = 0; i < 11; i++) {
            if (i == 10) {
                Rectangle r = new Rectangle(rectWidth, rectHeight);
                r.setFill(Paint.valueOf("RED"));
                grid.add(r, 10, 10);
            }
            else {
                Rectangle r = new Rectangle(rectWidth, heightHalf);
                r.setFill(Paint.valueOf("RED"));
                grid.add(r, 10, i+1);

            }
        }
        for (int i = 0; i < 11; i++) {
            if (i == 10) {
                Rectangle r = new Rectangle(rectWidth, rectHeight);
                r.setFill(Paint.valueOf("BLUE"));
                grid.add(r, 0, 10);
            }
            else {
                Rectangle r = new Rectangle(rectWidth, heightHalf);
                r.setFill(Paint.valueOf("BLUE"));
                grid.add(r, 0, i+1);

            }
        }

        for (int i = 0; i < 9; i++) {
            Rectangle r = new Rectangle(widthHalf, rectHeight);
            r.setFill(Paint.valueOf("YELLOW"));
            grid.add(r, i+1, 10);
        }

        Scene scene = new Scene(grid, 800, 800);
        primaryStage.setScene(scene);

        primaryStage.show();

/*
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password: ");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(event -> {
            actiontarget.setFill(Color.FIREBRICK);
            actiontarget.setText(userTextField.getText());

        });*/
    }


}