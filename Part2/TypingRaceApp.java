import javafx.application.Application;
import javafx.stage.Stage;
import java.util.*;

public class TypingRaceApp extends Application {

    private Stage primaryStage;
    private List<LeaderboardEntry> leaderboard = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Typing Race Simulator");
        showHomeScreen();
        stage.show();
    }

    public void showHomeScreen() {
        new HomeScreen(this).show();
    }

    public void showLeaderboard() {
        new LeaderboardScreen(this).show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public LeaderboardEntry getOrCreateEntry(String name) {
        for (LeaderboardEntry e : leaderboard) {
            if (e.getName().equalsIgnoreCase(name)) return e;
        }
        LeaderboardEntry newEntry = new LeaderboardEntry(name);
        leaderboard.add(newEntry);
        return newEntry;
    }

    public List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> sorted = new ArrayList<>(leaderboard);
        sorted.sort((a, b) -> b.getTotalPoints() - a.getTotalPoints());
        return sorted;
    }

    public static void main(String[] args) {
        launch(args);
    }
}