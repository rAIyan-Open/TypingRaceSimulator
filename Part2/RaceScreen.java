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

    // Typist objects for proper simulation
    private List<Typist> typists = new ArrayList<>();

    // Per-typist stat tracking
    private List<Integer> totalKeystrokes = new ArrayList<>();
    private List<Integer> correctKeystrokes = new ArrayList<>();
    private List<Integer> burnoutCounts = new ArrayList<>();
    private List<Boolean> wasBurntOut = new ArrayList<>(); // track previous burnout state

    // Timing for WPM
    private long raceStartTime;

    private List<ProgressBar> progressBars = new ArrayList<>();
    private List<Label> statusLabels = new ArrayList<>();
    private List<TextFlow> passageViews = new ArrayList<>(); // per-typist highlighted passage

    private Label feedbackLabel;
    private TextField humanInput;
    private Button nextTurnBtn;
    private VBox content;

    private static final Random rand = new Random();

    // Accuracy constants (matching TypingRace.java)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int SLIDE_BACK_AMOUNT = 2;
    private static final int BURNOUT_DURATION = 3;

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

        // Create Typist objects with default accuracy based on index
        double[] defaultAccuracies = {0.85, 0.75, 0.65, 0.70, 0.80, 0.60};
        for (int i = 0; i < names.size(); i++) {
            double acc = defaultAccuracies[Math.min(i, defaultAccuracies.length - 1)];
            if (nightShift) acc = Math.max(0.01, acc - 0.1); // Night Shift modifier
            Typist t = new Typist('?', names.get(i), acc);
            typists.add(t);

            totalKeystrokes.add(0);
            correctKeystrokes.add(0);
            burnoutCounts.add(0);
            wasBurntOut.add(false);

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

        // RACERS BOX (progress bars + highlighted passage per typist)
        VBox racersBox = new VBox(16);
        racersBox.setStyle("-fx-background-color: #16213e; -fx-padding: 16; -fx-background-radius: 8;");

        Text racersTitle = new Text("RACERS");
        racersTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        racersTitle.setFill(Color.web("#e94560"));
        racersBox.getChildren().add(racersTitle);

        for (int i = 0; i < names.size(); i++) {
            VBox typistBox = new VBox(6);

            // Name + progress bar row
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(names.get(i) + (isHuman.get(i) ? " 👤" : " 🤖"));
            nameLabel.setTextFill(Color.web("#e0e0e0"));
            nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            nameLabel.setPrefWidth(160);

            ProgressBar pb = new ProgressBar(0);
            pb.setPrefWidth(400);
            pb.setPrefHeight(22);
            pb.setStyle("-fx-accent: #e94560;");
            progressBars.add(pb);

            Label statusLabel = new Label("0 / " + passageLength);
            statusLabel.setTextFill(Color.web("#a8a8b3"));
            statusLabel.setFont(Font.font("Arial", 12));
            statusLabels.add(statusLabel);

            row.getChildren().addAll(nameLabel, pb, statusLabel);

            // Highlighted passage view for this typist
            TextFlow tf = buildPassageView(0);
            tf.setStyle("-fx-background-color: #0f3460; -fx-padding: 8; -fx-background-radius: 6;");
            tf.setMaxWidth(Double.MAX_VALUE);
            passageViews.add(tf);

            typistBox.getChildren().addAll(row, tf);
            racersBox.getChildren().add(typistBox);
        }
        content.getChildren().add(racersBox);

        // FEEDBACK LABEL
        feedbackLabel = new Label(humanIndex >= 0
                ? "Your turn! Type the passage and press Enter:"
                : "Race started! Click SIMULATE NEXT TURN to advance.");
        feedbackLabel.setTextFill(Color.web("#a8a8b3"));
        feedbackLabel.setFont(Font.font("Arial", 13));
        feedbackLabel.setStyle("-fx-background-color: #16213e; -fx-padding: 12; -fx-background-radius: 8;");
        feedbackLabel.setMaxWidth(Double.MAX_VALUE);
        content.getChildren().add(feedbackLabel);

        // HUMAN INPUT
        if (humanIndex >= 0) {
            VBox inputBox = new VBox(8);
            inputBox.setStyle("-fx-background-color: #16213e; -fx-padding: 12; -fx-background-radius: 8;");

            Label inputLabel = new Label("Type here:");
            inputLabel.setTextFill(Color.web("#a8a8b3"));
            inputLabel.setFont(Font.font("Arial", 13));

            humanInput = new TextField();
            humanInput.setPromptText("Start typing the passage...");
            humanInput.setStyle(
                "-fx-background-color: #0f3460; -fx-text-fill: white; " +
                "-fx-prompt-text-fill: #555; -fx-border-color: #e94560; " +
                "-fx-border-radius: 6; -fx-background-radius: 6; " +
                "-fx-padding: 10; -fx-font-size: 14px;");
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

        app.getPrimaryStage().setScene(new Scene(root, 900, 680));

        // Start race timer
        raceStartTime = System.currentTimeMillis();
    }

    // Builds a TextFlow showing completed (green), cursor (white box), remaining (grey)
    private TextFlow buildPassageView(int cursorPos) {
        TextFlow tf = new TextFlow();

        // Completed portion (green)
        if (cursorPos > 0) {
            Text done = new Text(passage.substring(0, Math.min(cursorPos, passageLength)));
            done.setFill(Color.web("#4caf50"));
            done.setFont(Font.font("Courier New", 13));
            tf.getChildren().add(done);
        }

        // Cursor character (highlighted)
        if (cursorPos < passageLength) {
            Text cursor = new Text(String.valueOf(passage.charAt(cursorPos)));
            cursor.setFill(Color.web("#1a1a2e"));
            cursor.setStyle("-fx-background-color: #e94560;");
            cursor.setFont(Font.font("Courier New", FontWeight.BOLD, 13));
            tf.getChildren().add(cursor);

            // Remaining portion (grey)
            if (cursorPos + 1 < passageLength) {
                Text remaining = new Text(passage.substring(cursorPos + 1));
                remaining.setFill(Color.web("#a8a8b3"));
                remaining.setFont(Font.font("Courier New", 13));
                tf.getChildren().add(remaining);
            }
        }

        return tf;
    }

    // Refreshes the passage view TextFlow for a given typist index
    private void refreshPassageView(int index) {
        int pos = typists.get(index).getProgress();
        TextFlow tf = passageViews.get(index);
        tf.getChildren().clear();

        TextFlow updated = buildPassageView(pos);
        tf.getChildren().addAll(updated.getChildren());
    }

    private void handleHumanInput() {
        if (raceOver) return;
        String typed = humanInput.getText();
        humanInput.clear();
        if (typed.isEmpty()) return;

        int pos = typists.get(humanIndex).getProgress();
        String remaining = passage.substring(Math.min(pos, passageLength));
        int correct = 0;
        int total = Math.min(typed.length(), remaining.length());

        for (int i = 0; i < total; i++) {
            totalKeystrokes.set(humanIndex, totalKeystrokes.get(humanIndex) + 1);
            if (typed.charAt(i) == remaining.charAt(i)) {
                correct++;
                correctKeystrokes.set(humanIndex, correctKeystrokes.get(humanIndex) + 1);
            } else if (!autocorrect) {
                break; // stop on first wrong char unless autocorrect
            }
        }

        if (autocorrect) correct = Math.max(correct, typed.length() / 2);

        for (int i = 0; i < correct; i++) typists.get(humanIndex).typeCharacter();
        updateUI(humanIndex);

        feedbackLabel.setText("✅ " + correct + " correct character(s) this turn!");
        feedbackLabel.setTextFill(Color.web("#4caf50"));

        if (checkWin(humanIndex)) return;

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

        for (int i = 0; i < typists.size(); i++) {
            if (!isHuman.get(i)) {
                Typist t = typists.get(i);

                // Track burnout events
                if (!wasBurntOut.get(i) && t.isBurntOut()) {
                    burnoutCounts.set(i, burnoutCounts.get(i) + 1);
                }
                wasBurntOut.set(i, t.isBurntOut());

                advanceTypist(t, i);
                updateUI(i);

                log.append(names.get(i))
                   .append(t.isBurntOut() ? " 💤" : " +" + t.getProgress())
                   .append("  ");

                if (checkWin(i)) return;
            }
        }

        if (humanIndex == -1) {
            feedbackLabel.setText(log.toString());
        }
    }

    // Mirrors TypingRace.advanceTypist logic using actual Typist methods
    private void advanceTypist(Typist t, int index) {
        if (t.isBurntOut()) {
            t.recoverFromBurnout();
            return;
        }

        int slideBack = autocorrect ? Math.max(1, SLIDE_BACK_AMOUNT / 2) : SLIDE_BACK_AMOUNT;
        int caffeineBoost = (caffeine && currentTurn <= 10) ? 2 : 0;

        // Attempt to type a character
        if (Math.random() < t.getAccuracy() + caffeineBoost * 0.05) {
            t.typeCharacter();
            totalKeystrokes.set(index, totalKeystrokes.get(index) + 1);
            correctKeystrokes.set(index, correctKeystrokes.get(index) + 1);
        }

        // Mistype check
        if (Math.random() > t.getAccuracy() - MISTYPE_BASE_CHANCE) {
            t.slideBack(slideBack);
            totalKeystrokes.set(index, totalKeystrokes.get(index) + 1);
        }

        // Burnout check (accuracy² capped at 0.05)
        double burnoutChance = Math.min(0.05, t.getAccuracy() * t.getAccuracy());
        if (caffeine && currentTurn > 10) burnoutChance = Math.min(0.15, burnoutChance * 3);
        if (Math.random() < burnoutChance) {
            t.burnOut(BURNOUT_DURATION);
            if (!wasBurntOut.get(index)) {
                burnoutCounts.set(index, burnoutCounts.get(index) + 1);
            }
        }
    }

    private void updateUI(int index) {
        int pos = typists.get(index).getProgress();
        progressBars.get(index).setProgress((double) pos / passageLength);

        String status = pos + " / " + passageLength;
        if (typists.get(index).isBurntOut()) {
            status += " 💤 burnt out (" + typists.get(index).getBurnoutTurnsRemaining() + ")";
            statusLabels.get(index).setTextFill(Color.web("#e94560"));
        } else {
            statusLabels.get(index).setTextFill(Color.web("#a8a8b3"));
        }
        statusLabels.get(index).setText(status);

        refreshPassageView(index);
    }

    private boolean checkWin(int index) {
        if (typists.get(index).getProgress() >= passageLength) {
            raceOver = true;
            long raceTimeMs = System.currentTimeMillis() - raceStartTime;

            if (nextTurnBtn != null) nextTurnBtn.setDisable(true);
            if (humanInput != null) humanInput.setDisable(true);

            feedbackLabel.setText("🏆 " + names.get(index) + " wins the race!");
            feedbackLabel.setTextFill(Color.web("#e94560"));

            // Accuracy adjustment for winner (spec requirement)
            typists.get(index).setAccuracy(typists.get(index).getAccuracy() + 0.01);

            // Build stats for passing to StatsScreen
            // WPM = (passageLength / 5) / (timeInMinutes)
            List<Double> wpmList = new ArrayList<>();
            List<Double> accuracyList = new ArrayList<>();
            List<Integer> burnoutList = new ArrayList<>();

            double timeInMinutes = raceTimeMs / 60000.0;
            double words = passageLength / 5.0;

            for (int i = 0; i < typists.size(); i++) {
                double wpm = timeInMinutes > 0 ? words / timeInMinutes : 0;
                // Only winner gets full WPM; others scale by progress
                double scale = (double) typists.get(i).getProgress() / passageLength;
                wpmList.add(i == index ? wpm : wpm * scale);

                int total = totalKeystrokes.get(i);
                int correct = correctKeystrokes.get(i);
                double acc = total > 0 ? (double) correct / total * 100.0 : 100.0;
                accuracyList.add(acc);

                burnoutList.add(burnoutCounts.get(i));
            }

            // Transition to stats screen after short delay
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(e ->
                app.showStatsScreen(names, wpmList, accuracyList, burnoutList,
                                    typists, index, raceTimeMs)
            );
            delay.play();

            return true;
        }
        return false;
    }
}