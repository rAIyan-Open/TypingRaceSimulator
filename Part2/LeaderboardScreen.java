import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.*;

public class LeaderboardScreen {

    private TypingRaceApp app;

    public LeaderboardScreen(TypingRaceApp app) {
        this.app = app;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // TOP BAR
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #16213e;");
        Text pageTitle = new Text("🏅  LEADERBOARD");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pageTitle.setFill(Color.web("#e94560"));
        topBar.getChildren().add(pageTitle);
        root.setTop(topBar);

        // MAIN CONTENT
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #1a1a2e;");

        List<LeaderboardEntry> entries = app.getLeaderboard();

        if (entries.isEmpty()) {
            // Empty state
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            Text emptyIcon = new Text("🏁");
            emptyIcon.setFont(Font.font("Arial", 48));
            Text emptyMsg = new Text("No races completed yet!");
            emptyMsg.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            emptyMsg.setFill(Color.web("#a8a8b3"));
            Text emptyHint = new Text("Complete a race to see rankings here.");
            emptyHint.setFont(Font.font("Arial", 13));
            emptyHint.setFill(Color.web("#555"));
            emptyBox.getChildren().addAll(emptyIcon, emptyMsg, emptyHint);
            content.getChildren().add(emptyBox);

        } else {
            // RANKINGS TABLE
            VBox tableBox = new VBox(10);
            tableBox.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");

            Text tableTitle = new Text("GLOBAL RANKINGS");
            tableTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            tableTitle.setFill(Color.web("#e94560"));
            tableBox.getChildren().add(tableTitle);

            // Header
            HBox header = buildRow("RANK", "TYPIST", "POINTS", "WINS", "RACES", "BEST WPM", "BADGE", true);
            tableBox.getChildren().add(header);

            Separator sep = new Separator();
            sep.setStyle("-fx-background-color: #e94560;");
            tableBox.getChildren().add(sep);

            // Entries
            String[] medals = {"🥇", "🥈", "🥉"};
            for (int i = 0; i < entries.size(); i++) {
                LeaderboardEntry e = entries.get(i);
                String rank = i < 3 ? medals[i] : "#" + (i + 1);
                String bestWpm = String.format("%.1f", e.getBestWPM());

                HBox row = buildRow(
                    rank,
                    e.getName(),
                    String.valueOf(e.getTotalPoints()),
                    String.valueOf(e.getWins()),
                    String.valueOf(e.getRacesPlayed()),
                    bestWpm,
                    e.getBadge(),
                    false
                );

                // Highlight top 3
                if (i == 0) row.setStyle("-fx-background-color: rgba(233,69,96,0.15); -fx-background-radius: 6; -fx-padding: 4;");
                else if (i == 1) row.setStyle("-fx-background-color: rgba(255,255,255,0.05); -fx-background-radius: 6; -fx-padding: 4;");
                else if (i == 2) row.setStyle("-fx-background-color: rgba(255,255,255,0.03); -fx-background-radius: 6; -fx-padding: 4;");

                tableBox.getChildren().add(row);
            }

            content.getChildren().add(tableBox);

            // BADGE LEGEND
            VBox legendBox = new VBox(8);
            legendBox.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");
            Text legendTitle = new Text("BADGE GUIDE");
            legendTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            legendTitle.setFill(Color.web("#e94560"));
            legendBox.getChildren().add(legendTitle);

            String[][] badges = {
                {"⚡ Speed Demon", "3 consecutive wins"},
                {"🔥 Iron Fingers", "5 races with zero burnouts"},
                {"🏆 Champion",    "3 or more total wins"},
                {"🎯 Veteran",     "3 or more races played"},
                {"🌱 Rookie",      "Just getting started"}
            };

            for (String[] badge : badges) {
                HBox badgeRow = new HBox(12);
                badgeRow.setAlignment(Pos.CENTER_LEFT);
                Label badgeName = new Label(badge[0]);
                badgeName.setTextFill(Color.web("#e0e0e0"));
                badgeName.setFont(Font.font("Arial", FontWeight.BOLD, 12));
                badgeName.setPrefWidth(180);
                Label badgeDesc = new Label(badge[1]);
                badgeDesc.setTextFill(Color.web("#a8a8b3"));
                badgeDesc.setFont(Font.font("Arial", 12));
                badgeRow.getChildren().addAll(badgeName, badgeDesc);
                legendBox.getChildren().add(badgeRow);
            }
            content.getChildren().add(legendBox);
        }

        // BUTTONS
        HBox btnRow = new HBox(16);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        Button homeBtn = new Button("🏠  HOME");
        homeBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        homeBtn.setTextFill(Color.WHITE);
        homeBtn.setPrefHeight(44);
        homeBtn.setPrefWidth(160);
        homeBtn.setStyle("-fx-background-color: #e94560; -fx-background-radius: 8;");
        homeBtn.setOnMouseEntered(e -> homeBtn.setOpacity(0.85));
        homeBtn.setOnMouseExited(e -> homeBtn.setOpacity(1.0));
        homeBtn.setOnAction(e -> app.showHomeScreen());

        Button raceBtn = new Button("🏁  NEW RACE");
        raceBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        raceBtn.setTextFill(Color.WHITE);
        raceBtn.setPrefHeight(44);
        raceBtn.setPrefWidth(160);
        raceBtn.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 8;");
        raceBtn.setOnMouseEntered(e -> raceBtn.setOpacity(0.85));
        raceBtn.setOnMouseExited(e -> raceBtn.setOpacity(1.0));
        raceBtn.setOnAction(e -> app.showHomeScreen());

        btnRow.getChildren().addAll(homeBtn, raceBtn);
        content.getChildren().add(btnRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        app.getPrimaryStage().setScene(new Scene(root, 900, 650));
    }

    private HBox buildRow(String rank, String name, String points, String wins,
                           String races, String wpm, String badge, boolean isHeader) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 0, 6, 0));

        String[] values = {rank, name, points, wins, races, wpm, badge};
        double[] widths = {60, 160, 80, 70, 70, 100, 160};

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