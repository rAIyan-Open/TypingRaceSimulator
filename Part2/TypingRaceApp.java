import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class TypingRaceApp extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Typing Race Simulator");
        showHomeScreen();
        stage.show();
    }

    public void showHomeScreen() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        VBox center = new VBox(20);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(60));

        HBox titleRow = new HBox(16);
        titleRow.setAlignment(Pos.CENTER);
        Text icon = new Text("⌨");
        icon.setFont(Font.font("Arial", 48));
        icon.setFill(Color.web("#e94560"));
        Text title = new Text("TYPING RACE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 52));
        title.setFill(Color.web("#e94560"));
        titleRow.getChildren().addAll(icon, title);

        Text subtitle = new Text("SIMULATOR");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        subtitle.setFill(Color.web("#4a90d9"));

        Text tagline = new Text("How fast are your fingers?");
        tagline.setFont(Font.font("Arial", FontPosture.ITALIC, 15));
        tagline.setFill(Color.web("#a8a8b3"));

        Button startBtn = menuButton("▶  START RACE", "#e94560");
        Button leaderBtn = menuButton("🏆  LEADERBOARD", "#0f3460");
        Button quitBtn = menuButton("✕  QUIT", "#2a2a3e");

        startBtn.setOnAction(e -> showSetupScreen());
        leaderBtn.setOnAction(e -> showLeaderboard());
        quitBtn.setOnAction(e -> primaryStage.close());

        center.getChildren().addAll(titleRow, subtitle, tagline, startBtn, leaderBtn, quitBtn);
        root.setCenter(center);
        primaryStage.setScene(new Scene(root, 900, 600));
    }

    public void showSetupScreen() {
        new SetupScreen(this).show();
    }

    public void showLeaderboard() {
        System.out.println("Leaderboard coming soon!");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private Button menuButton(String text, String color) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        btn.setTextFill(Color.WHITE);
        btn.setPrefWidth(320);
        btn.setPrefHeight(52);
        btn.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.85));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}