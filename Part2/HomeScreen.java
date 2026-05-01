import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class HomeScreen {

    private TypingRaceApp app;

    public HomeScreen(TypingRaceApp app) {
        this.app = app;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        VBox content = new VBox(16);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(80));

        // Icon + Title row
        HBox titleRow = new HBox(16);
        titleRow.setAlignment(Pos.CENTER);
        Text icon = new Text("⌨");
        icon.setFont(Font.font("Arial", 48));
        icon.setFill(Color.web("#e94560"));
        Text title = new Text("TYPING RACE");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setFill(Color.web("#e94560"));
        titleRow.getChildren().addAll(icon, title);

        // Subtitle
        Text subtitle = new Text("SIMULATOR");
        subtitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        subtitle.setFill(Color.web("#0f3460"));

        // Tagline
        Text tagline = new Text("How fast are your fingers?");
        tagline.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
        tagline.setFill(Color.web("#a8a8b3"));

        // Buttons
        Button startBtn = new Button("▶  START RACE");
        startBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        startBtn.setTextFill(Color.WHITE);
        startBtn.setPrefWidth(280);
        startBtn.setPrefHeight(50);
        startBtn.setStyle("-fx-background-color: #e94560; -fx-background-radius: 8;");
        startBtn.setOnMouseEntered(e -> startBtn.setOpacity(0.85));
        startBtn.setOnMouseExited(e -> startBtn.setOpacity(1.0));
        startBtn.setOnAction(e -> app.showSetupScreen());

        Button leaderboardBtn = new Button("🏆  LEADERBOARD");
        leaderboardBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        leaderboardBtn.setTextFill(Color.WHITE);
        leaderboardBtn.setPrefWidth(280);
        leaderboardBtn.setPrefHeight(50);
        leaderboardBtn.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 8;");
        leaderboardBtn.setOnMouseEntered(e -> leaderboardBtn.setOpacity(0.85));
        leaderboardBtn.setOnMouseExited(e -> leaderboardBtn.setOpacity(1.0));
        leaderboardBtn.setOnAction(e -> app.showLeaderboard());

        Button quitBtn = new Button("✕  QUIT");
        quitBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        quitBtn.setTextFill(Color.web("#a8a8b3"));
        quitBtn.setPrefWidth(280);
        quitBtn.setPrefHeight(50);
        quitBtn.setStyle("-fx-background-color: #16213e; -fx-background-radius: 8;");
        quitBtn.setOnMouseEntered(e -> quitBtn.setOpacity(0.85));
        quitBtn.setOnMouseExited(e -> quitBtn.setOpacity(1.0));
        quitBtn.setOnAction(e -> System.exit(0));

        content.getChildren().addAll(titleRow, subtitle, tagline, startBtn, leaderboardBtn, quitBtn);
        root.setCenter(content);

        app.getPrimaryStage().setScene(new Scene(root, 900, 600));
    }
}