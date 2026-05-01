import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;
import javafx.animation.*;
import java.util.*;

public class StatsScreen {

    private TypingRaceApp app;
    private List<String> names;
    private List<Double> wpmList;
    private List<Double> accuracyList;
    private List<Integer> burnoutList;
    private List<Typist> typists;
    private int winnerIndex;
    private long raceTimeMs;

    public StatsScreen(TypingRaceApp app, List<String> names, List<Double> wpmList,
                       List<Double> accuracyList, List<Integer> burnoutList,
                       List<Typist> typists, int winnerIndex, long raceTimeMs) {
        this.app = app;
        this.names = names;
        this.wpmList = wpmList;
        this.accuracyList = accuracyList;
        this.burnoutList = burnoutList;
        this.typists = typists;
        this.winnerIndex = winnerIndex;
        this.raceTimeMs = raceTimeMs;

        // Save results to leaderboard
        for (int i = 0; i < names.size(); i++) {
            int position = (i == winnerIndex) ? 1 : i + 2;
            LeaderboardEntry entry = app.getOrCreateEntry(names.get(i));
            entry.addRaceResult(position, wpmList.get(i), burnoutList.get(i));
        }
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // TOP BAR
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #16213e;");
        Text pageTitle = new Text("📊  RACE RESULTS");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pageTitle.setFill(Color.web("#e94560"));
        topBar.getChildren().add(pageTitle);
        root.setTop(topBar);

        // MAIN CONTENT
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // WINNER BANNER
        long seconds = raceTimeMs / 1000;
        VBox winnerBanner = new VBox(6);
        winnerBanner.setAlignment(Pos.CENTER);
        winnerBanner.setStyle("-fx-background-color: #e94560; -fx-padding: 16; -fx-background-radius: 10;");
        Text winnerText = new Text("🏆  " + names.get(winnerIndex) + " WINS!");
        winnerText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        winnerText.setFill(Color.WHITE);
        Text timeText = new Text("Race completed in " + seconds + " seconds");
        timeText.setFont(Font.font("Arial", 14));
        timeText.setFill(Color.web("#ffe0e0"));
        winnerBanner.getChildren().addAll(winnerText, timeText);
        content.getChildren().add(winnerBanner);

        // STATS TABLE
        VBox statsBox = new VBox(12);
        statsBox.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");

        Text statsTitle = new Text("PERFORMANCE BREAKDOWN");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        statsTitle.setFill(Color.web("#e94560"));
        statsBox.getChildren().add(statsTitle);

        // Header row
        HBox header = buildRow("TYPIST", "WPM", "ACCURACY", "BURNOUTS", "ACCURACY CHANGE", true);
        statsBox.getChildren().add(header);

        // Divider
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #e94560;");
        statsBox.getChildren().add(sep);

        // One row per typist
        for (int i = 0; i < names.size(); i++) {
            String wpm = String.format("%.1f", wpmList.get(i));
            String acc = String.format("%.1f%%", accuracyList.get(i));
            String burnouts = String.valueOf(burnoutList.get(i));
            // Accuracy change: winner gets +0.01, burnout reduces
            String accChange = i == winnerIndex ? "+0.01 ▲" :
                               burnoutList.get(i) > 0 ? "-0.01 ▼" : "±0.00";

            HBox row = buildRow(
                names.get(i) + (i == winnerIndex ? " 🏆" : ""),
                wpm, acc, burnouts, accChange, false
            );
            statsBox.getChildren().add(row);
        }

        content.getChildren().add(statsBox);

        // BUTTONS
        HBox btnRow = new HBox(16);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        Button leaderboardBtn = new Button("🏅  VIEW LEADERBOARD");
        leaderboardBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        leaderboardBtn.setTextFill(Color.WHITE);
        leaderboardBtn.setPrefHeight(44);
        leaderboardBtn.setPrefWidth(220);
        leaderboardBtn.setStyle("-fx-background-color: #e94560; -fx-background-radius: 8;");
        leaderboardBtn.setOnMouseEntered(e -> leaderboardBtn.setOpacity(0.85));
        leaderboardBtn.setOnMouseExited(e -> leaderboardBtn.setOpacity(1.0));
        leaderboardBtn.setOnAction(e -> app.showLeaderboard());

        Button homeBtn = new Button("🏠  HOME");
        homeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        homeBtn.setTextFill(Color.WHITE);
        homeBtn.setPrefHeight(44);
        homeBtn.setPrefWidth(140);
        homeBtn.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 8;");
        homeBtn.setOnMouseEntered(e -> homeBtn.setOpacity(0.85));
        homeBtn.setOnMouseExited(e -> homeBtn.setOpacity(1.0));
        homeBtn.setOnAction(e -> app.showHomeScreen());

        btnRow.getChildren().addAll(leaderboardBtn, homeBtn);
        content.getChildren().add(btnRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        app.getPrimaryStage().setScene(new Scene(root, 900, 600));
    }

    private HBox buildRow(String name, String wpm, String acc,
                           String burnouts, String accChange, boolean isHeader) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));

        String[] values = {name, wpm, acc, burnouts, accChange};
        double[] widths = {200, 100, 120, 120, 150};

        for (int i = 0; i < values.length; i++) {
            Label lbl = new Label(values[i]);
            lbl.setPrefWidth(widths[i]);
            lbl.setFont(isHeader
                ? Font.font("Arial", FontWeight.BOLD, 12)
                : Font.font("Arial", 13));
            lbl.setTextFill(isHeader
                ? Color.web("#a8a8b3")
                : Color.web("#e0e0e0"));
            row.getChildren().add(lbl);
        }
        return row;
    }
}