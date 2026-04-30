import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.animation.*;
import javafx.util.Duration;
import java.util.*;

public class RaceScreen {

    private TypingRaceApp app;
    private List<String> names;
    private List<Boolean> isHuman;
    private String passage;
    private boolean autocorrect, caffeine, nightShift;

    private int passageLength;
    private int currentTurn = 0;
    private boolean raceOver = false;
    private int humanIndex = -1;

    private List<Integer> positions = new ArrayList<>();
    private List<ProgressBar> progressBars = new ArrayList<>();
    private List<Label> statusLabels = new ArrayList<>();

    private Label passageLabel;
    private Label feedbackLabel;
    private TextField humanInput;
    private Button nextTurnBtn;
    private VBox content;

    private static final Random rand = new Random();

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
            if (isHuman.get(i)) humanIndex = i;
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
        content = new VBox(20);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.setStyle("-fx-background-color: #1a1a2e;");

        // PASSAGE DISPLAY
        passageLabel = new Label(passage);
        passageLabel.setWrapText(true);
        passageLabel.setFont(Font.font("Courier New", 15));
        passageLabel.setTextFill(Color.web("#a8a8b3"));
        passageLabel.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");
        passageLabel.setMaxWidth(Double.MAX_VALUE);
        content.getChildren().add(passageLabel);

        // PROGRESS BARS
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

        // FEEDBACK LABEL
        feedbackLabel = new Label(humanIndex >= 0
            ? "Your turn! Type as much of the passage as you can and press Enter:"
            : "Race started! Click SIMULATE NEXT TURN to advance the race.");
        feedbackLabel.setTextFill(Color.web("#a8a8b3"));
        feedbackLabel.setFont(Font.font("Arial", 13));
        feedbackLabel.setStyle("-fx-background-color: #16213e; -fx-padding: 12; -fx-background-radius: 8;");
        feedbackLabel.setMaxWidth(Double.MAX_VALUE);
        content.getChildren().add(feedbackLabel);

        // HUMAN INPUT (only shown if there's a human player)
        if (humanIndex >= 0) {
            VBox inputBox = new VBox(8);
            inputBox.setStyle("-fx-background-color: #16213e; -fx-padding: 12; -fx-background-radius: 8;");

            Label inputLabel = new Label("Type here:");
            inputLabel.setTextFill(Color.web("#a8a8b3"));
            inputLabel.setFont(Font.font("Arial", 13));

            humanInput = new TextField();
            humanInput.setPromptText("Start typing the passage...");
            humanInput.setStyle("-fx-background-color: #0f3460; -fx-text-fill: white; -fx-prompt-text-fill: #555; -fx-border-color: #e94560; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-size: 14px;");
            humanInput.setOnAction(e -> handleHumanInput());

            inputBox.getChildren().addAll(inputLabel, humanInput);
            content.getChildren().add(inputBox);
        }

        // NEXT TURN BUTTON (CPU only mode)
        nextTurnBtn = new Button("⏭  SIMULATE NEXT TURN");
        nextTurnBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nextTurnBtn.setTextFill(Color.WHITE);
        nextTurnBtn.setPrefHeight(44);
        nextTurnBtn.setPrefWidth(250);
        nextTurnBtn.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 8;");
        nextTurnBtn.setOnMouseEntered(e -> nextTurnBtn.setOpacity(0.85));
        nextTurnBtn.setOnMouseExited(e -> nextTurnBtn.setOpacity(1.0));
        nextTurnBtn.setOnAction(e -> { if (!raceOver) runCpuTurns(); });
        nextTurnBtn.setVisible(humanIndex == -1);

        HBox btnRow = new HBox(nextTurnBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().add(btnRow);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setStyle("-fx-background: #1a1a2e; -fx-background-color: #1a1a2e;");
        scrollPane.setFitToWidth(true);
        root.setCenter(scrollPane);

        app.getPrimaryStage().setScene(new Scene(root, 900, 620));
    }

    private void handleHumanInput() {
        if (raceOver) return;
        String typed = humanInput.getText().trim();
        humanInput.clear();
        if (typed.isEmpty()) return;

        int correct = countCorrectChars(typed, humanIndex);
        updatePosition(humanIndex, correct);

        feedbackLabel.setText("✅ You typed " + correct + " correct character(s) this turn!");
        feedbackLabel.setTextFill(Color.web("#4caf50"));

        if (checkWin(humanIndex)) return;

        // CPU takes turns after human
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(e -> {
            runCpuTurns();
            if (!raceOver) {
                feedbackLabel.setText("Your turn! Keep typing the passage and press Enter:");
                feedbackLabel.setTextFill(Color.web("#a8a8b3"));
            }
        });
        pause.play();
    }

    private void runCpuTurns() {
        currentTurn++;
        StringBuilder log = new StringBuilder("Turn " + currentTurn + ": ");

        for (int i = 0; i < names.size(); i++) {
            if (!isHuman.get(i)) {
                int advance = getCpuAdvance();
                updatePosition(i, advance);
                log.append(names.get(i)).append(" +").append(advance).append("  ");
                if (checkWin(i)) return;
            }
        }

        if (humanIndex == -1) {
            feedbackLabel.setText(log.toString());
        }
    }

    private int getCpuAdvance() {
        int base = 5 + rand.nextInt(6);
        if (caffeine && currentTurn <= 10) base += 3;
        if (nightShift) base = Math.max(1, base - 2);
        if (caffeine && currentTurn > 10 && rand.nextInt(3) == 0) base = Math.max(0, base - 4);
        return base;
    }

    private int countCorrectChars(String typed, int index) {
        int pos = positions.get(index);
        String remaining = passage.substring(Math.min(pos, passageLength));
        int count = 0;
        for (int i = 0; i < Math.min(typed.length(), remaining.length()); i++) {
            if (typed.charAt(i) == remaining.charAt(i)) count++;
            else if (!autocorrect) break;
        }
        if (autocorrect) count = Math.max(count, typed.length() / 2);
        return count;
    }

    private void updatePosition(int index, int advance) {
        int newPos = Math.min(positions.get(index) + advance, passageLength);
        positions.set(index, newPos);
        progressBars.get(index).setProgress((double) newPos / passageLength);
        statusLabels.get(index).setText(newPos + " / " + passageLength);
    }

    private boolean checkWin(int index) {
        if (positions.get(index) >= passageLength) {
            raceOver = true;
            if (nextTurnBtn != null) nextTurnBtn.setDisable(true);
            if (humanInput != null) humanInput.setDisable(true);

            passageLabel.setText("🏆 " + names.get(index) + " wins the race!");
            passageLabel.setTextFill(Color.web("#e94560"));
            passageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            feedbackLabel.setText("Race over! " + names.get(index) + " finished first!");
            feedbackLabel.setTextFill(Color.web("#e94560"));

            // PLAY AGAIN button
            Button playAgainBtn = new Button("🔄  PLAY AGAIN");
            playAgainBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            playAgainBtn.setTextFill(Color.WHITE);
            playAgainBtn.setPrefHeight(45);
            playAgainBtn.setPrefWidth(200);
            playAgainBtn.setStyle("-fx-background-color: #e94560; -fx-background-radius: 8;");
            playAgainBtn.setOnAction(e -> app.showHomeScreen());

            content.getChildren().add(playAgainBtn);
            return true;
        }
        return false;
    }
}