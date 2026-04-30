import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.*;

public class RaceScreen {

    private TypingRaceApp app;
    private List<String> names;
    private List<Boolean> isHuman;
    private String passage;
    private boolean autocorrect, caffeine, nightShift;

    private int passageLength;
    private List<Integer> positions = new ArrayList<>();
    private List<ProgressBar> progressBars = new ArrayList<>();
    private List<Label> statusLabels = new ArrayList<>();

    public RaceScreen(TypingRaceApp app, List<String> names, List<Boolean> isHuman,
                      String passage, boolean autocorrect, boolean caffeine, boolean nightShift) {
        this.app = app;
        this.names = names;
        this.isHuman = isHuman;
        this.passage = passage;
        this.autocorrect = autocorrect;
        this.caffeine = caffeine;
        this.nightShift = nightShift;
        this.passageLength = passage.length();

        for (int i = 0; i < names.size(); i++) {
            positions.add(0);
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

        Text pageTitle = new Text("🏁  RACE IN PROGRESS");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pageTitle.setFill(Color.web("#e94560"));
        topBar.getChildren().add(pageTitle);
        root.setTop(topBar);

        // MAIN CONTENT
        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // PASSAGE DISPLAY
        Label passageLabel = new Label(passage);
        passageLabel.setWrapText(true);
        passageLabel.setFont(Font.font("Courier New", 15));
        passageLabel.setTextFill(Color.web("#a8a8b3"));
        passageLabel.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");
        passageLabel.setMaxWidth(Double.MAX_VALUE);
        content.getChildren().add(passageLabel);

        // PROGRESS BARS SECTION
        VBox racersBox = new VBox(14);
        racersBox.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");

        Text racersTitle = new Text("RACERS");
        racersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        racersTitle.setFill(Color.web("#e94560"));
        racersBox.getChildren().add(racersTitle);

        for (int i = 0; i < names.size(); i++) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(names.get(i) + (isHuman.get(i) ? " 👤" : " 🤖"));
            nameLabel.setTextFill(Color.web("#e0e0e0"));
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            nameLabel.setPrefWidth(150);

            ProgressBar pb = new ProgressBar(0);
            pb.setPrefWidth(450);
            pb.setPrefHeight(22);
            pb.setStyle("-fx-accent: #e94560;");
            progressBars.add(pb);

            Label statusLabel = new Label("0 / " + passageLength);
            statusLabel.setTextFill(Color.web("#a8a8b3"));
            statusLabel.setFont(Font.font("Arial", 12));
            statusLabels.add(statusLabel);

            row.getChildren().addAll(nameLabel, pb, statusLabel);
            racersBox.getChildren().add(row);
        }

        content.getChildren().add(racersBox);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        app.getPrimaryStage().setScene(new Scene(root, 900, 620));
    }
}