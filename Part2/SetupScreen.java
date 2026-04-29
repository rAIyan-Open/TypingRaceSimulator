import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

public class SetupScreen {

    private TypingRaceApp app;

    public SetupScreen(TypingRaceApp app) {
        this.app = app;
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
        backBtn.setOnAction(e -> app.showHomeScreen());

        Text pageTitle = new Text("  RACE SETUP");
        pageTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        pageTitle.setFill(Color.web("#e94560"));

        topBar.getChildren().addAll(backBtn, pageTitle);
        root.setTop(topBar);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scrollPane.setFitToWidth(true);

        VBox content = new VBox(24);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #1a1a2e;");

        content.getChildren().add(sectionLabel("📄  PASSAGE SELECTION"));

        ToggleGroup passageGroup = new ToggleGroup();
        HBox passageOptions = new HBox(12);
        passageOptions.setAlignment(Pos.CENTER_LEFT);

        RadioButton shortBtn  = styledRadio("Short (20 chars)", passageGroup);
        RadioButton medBtn    = styledRadio("Medium (60 chars)", passageGroup);
        RadioButton longBtn   = styledRadio("Long (120 chars)", passageGroup);
        RadioButton customBtn = styledRadio("Custom", passageGroup);
        medBtn.setSelected(true);

        passageOptions.getChildren().addAll(shortBtn, medBtn, longBtn, customBtn);

        TextField customPassage = new TextField();
        customPassage.setPromptText("Enter your custom passage here...");
        customPassage.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-prompt-text-fill: #555; -fx-border-color: #0f3460; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
        customPassage.setVisible(false);

        customBtn.setOnAction(e -> customPassage.setVisible(true));
        shortBtn.setOnAction(e -> customPassage.setVisible(false));
        medBtn.setOnAction(e -> customPassage.setVisible(false));
        longBtn.setOnAction(e -> customPassage.setVisible(false));

        content.getChildren().addAll(passageOptions, customPassage);

        content.getChildren().add(sectionLabel("👥  NUMBER OF TYPISTS"));

        ToggleGroup seatGroup = new ToggleGroup();
        HBox seatOptions = new HBox(12);
        seatOptions.setAlignment(Pos.CENTER_LEFT);
        for (int i = 2; i <= 6; i++) {
            RadioButton rb = styledRadio(String.valueOf(i), seatGroup);
            if (i == 3) rb.setSelected(true);
            seatOptions.getChildren().add(rb);
        }
        content.getChildren().add(seatOptions);

        content.getChildren().add(sectionLabel("⚙️  DIFFICULTY MODIFIERS"));

        CheckBox autocorrect = styledCheckbox("Autocorrect — slideBack amount is halved");
        CheckBox caffeine    = styledCheckbox("Caffeine Mode — speed boost for first 10 turns");
        CheckBox nightShift  = styledCheckbox("Night Shift — accuracy reduced across the board");

        content.getChildren().addAll(autocorrect, caffeine, nightShift);

        Button startRaceBtn = new Button("▶  START RACE");
        startRaceBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        startRaceBtn.setTextFill(Color.WHITE);
        startRaceBtn.setPrefWidth(300);
        startRaceBtn.setPrefHeight(50);
        startRaceBtn.setStyle("-fx-background-color: #e94560; -fx-background-radius: 8;");
        startRaceBtn.setOnMouseEntered(e -> startRaceBtn.setOpacity(0.85));
        startRaceBtn.setOnMouseExited(e -> startRaceBtn.setOpacity(1.0));
        startRaceBtn.setOnAction(e -> {
            int passageLength = 60;
            if (shortBtn.isSelected()) passageLength = 20;
            else if (longBtn.isSelected()) passageLength = 120;
            else if (customBtn.isSelected() && !customPassage.getText().isEmpty())
                passageLength = customPassage.getText().length();

            int seats = 3;
            for (Toggle t : seatGroup.getToggles()) {
                if (t.isSelected()) {
                    seats = Integer.parseInt(((RadioButton) t).getText());
                }
            }

            String passage = customBtn.isSelected() ? customPassage.getText() : getDefaultPassage(passageLength);

            new TypistSetupScreen(app, seats, passageLength, passage,
                autocorrect.isSelected(), caffeine.isSelected(), nightShift.isSelected()).show();
        });

        VBox startBox = new VBox(startRaceBtn);
        startBox.setAlignment(Pos.CENTER);
        startBox.setPadding(new Insets(10, 0, 0, 0));
        content.getChildren().add(startBox);

        scrollPane.setContent(content);
        root.setCenter(scrollPane);

        app.getPrimaryStage().setScene(new Scene(root, 900, 600));
    }

    private String getDefaultPassage(int length) {
        String full = "The quick brown fox jumps over the lazy dog. Pack my box with five dozen liquor jugs. How vexingly quick daft zebras jump!";
        return full.length() >= length ? full.substring(0, length) : full;
    }

    private Text sectionLabel(String text) {
        Text t = new Text(text);
        t.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        t.setFill(Color.web("#e94560"));
        return t;
    }

    private RadioButton styledRadio(String label, ToggleGroup group) {
        RadioButton rb = new RadioButton(label);
        rb.setToggleGroup(group);
        rb.setTextFill(Color.web("#e0e0e0"));
        rb.setFont(Font.font("Arial", 13));
        return rb;
    }

    private CheckBox styledCheckbox(String label) {
        CheckBox cb = new CheckBox(label);
        cb.setTextFill(Color.web("#e0e0e0"));
        cb.setFont(Font.font("Arial", 13));
        return cb;
    }
}