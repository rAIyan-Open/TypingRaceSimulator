import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import java.util.*;

public class TypistSetupScreen {

    private TypingRaceApp app;
    private int seats, passageLength;
    private String passage;
    private boolean autocorrect, caffeine, nightShift;

    private List<TextField> nameFields = new ArrayList<>();
    private List<ToggleGroup> typeGroups = new ArrayList<>();

    public TypistSetupScreen(TypingRaceApp app, int seats, int passageLength,
                              String passage, boolean autocorrect, boolean caffeine, boolean nightShift) {
        this.app = app;
        this.seats = seats;
        this.passageLength = passageLength;
        this.passage = passage;
        this.autocorrect = autocorrect;
        this.caffeine = caffeine;
        this.nightShift = nightShift;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(16, 24, 16, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #16213e;");

        Button backBtn = new Button("← Back");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #a8a8b3; -fx-font-size: 13px;");
        backBtn.setOnAction(e -> app.showSetupScreen());

        Text pageTitle = new Text("  TYPIST SETUP");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pageTitle.setFill(Color.web("#e94560"));

        topBar.getChildren().addAll(backBtn, pageTitle);
        root.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #1a1a2e;");

        for (int i = 0; i < seats; i++) {
            content.getChildren().add(buildTypistCard(i));
        }

        Button startBtn = new Button("▶  BEGIN RACE");
        startBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        startBtn.setTextFill(Color.WHITE);
        startBtn.setPrefWidth(300);
        startBtn.setPrefHeight(50);
        startBtn.setStyle("-fx-background-color: #e94560; -fx-background-radius: 8;");
        startBtn.setOnMouseEntered(e -> startBtn.setOpacity(0.85));
        startBtn.setOnMouseExited(e -> startBtn.setOpacity(1.0));
        startBtn.setOnAction(e -> startRace());

        VBox btnBox = new VBox(startBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(10, 0, 0, 0));
        content.getChildren().add(btnBox);

        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        app.getPrimaryStage().setScene(new Scene(root, 900, 600));
    }

    private VBox buildTypistCard(int index) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");

        Text header = new Text("TYPIST " + (index + 1));
        header.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        header.setFill(Color.web("#e94560"));

        HBox nameRow = new HBox(12);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label("Name:");
        nameLabel.setTextFill(Color.web("#a8a8b3"));
        nameLabel.setFont(Font.font("Arial", 13));

        TextField nameField = new TextField("Typist " + (index + 1));
        nameField.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-border-color: #e94560; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 10;");
        nameField.setPrefWidth(200);
        nameFields.add(nameField);
        nameRow.getChildren().addAll(nameLabel, nameField);

        HBox typeRow = new HBox(12);
        typeRow.setAlignment(Pos.CENTER_LEFT);
        Label typeLabel = new Label("Type:");
        typeLabel.setTextFill(Color.web("#a8a8b3"));
        typeLabel.setFont(Font.font("Arial", 13));

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton humanBtn = new RadioButton("Human");
        humanBtn.setToggleGroup(typeGroup);
        humanBtn.setTextFill(Color.web("#e0e0e0"));
        humanBtn.setFont(Font.font("Arial", 13));
        humanBtn.setSelected(index == 0);

        RadioButton cpuBtn = new RadioButton("CPU");
        cpuBtn.setToggleGroup(typeGroup);
        cpuBtn.setTextFill(Color.web("#e0e0e0"));
        cpuBtn.setFont(Font.font("Arial", 13));
        cpuBtn.setSelected(index != 0);

        typeGroups.add(typeGroup);
        typeRow.getChildren().addAll(typeLabel, humanBtn, cpuBtn);

        card.getChildren().addAll(header, nameRow, typeRow);
        return card;
    }

    private void startRace() {
        List<String> names = new ArrayList<>();
        List<Boolean> isHuman = new ArrayList<>();

        for (int i = 0; i < seats; i++) {
            names.add(nameFields.get(i).getText().trim().isEmpty()
                ? "Typist " + (i + 1) : nameFields.get(i).getText().trim());
            Toggle selected = typeGroups.get(i).getSelectedToggle();
            boolean human = selected != null && ((RadioButton) selected).getText().equals("Human");
            isHuman.add(human);
        }

        new RaceScreen(app, names, isHuman, passage, autocorrect, caffeine, nightShift).show();
    }
}