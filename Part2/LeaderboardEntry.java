public class LeaderboardEntry {
    private String name;
    private int totalPoints;
    private int wins;
    private int racesPlayed;
    private double bestWPM;
    private int totalBurnouts;
    private int consecutiveWins;

    public LeaderboardEntry(String name) {
        this.name = name;
        this.totalPoints = 0;
        this.wins = 0;
        this.racesPlayed = 0;
        this.bestWPM = 0.0;
        this.totalBurnouts = 0;
        this.consecutiveWins = 0;
    }

    public void addRaceResult(int position, double wpm, int burnouts) {
        racesPlayed++;
        totalBurnouts += burnouts;
        if (wpm > bestWPM) bestWPM = wpm;

        if (position == 1) {
            totalPoints += 3;
            wins++;
            consecutiveWins++;
        } else if (position == 2) {
            totalPoints += 2;
            consecutiveWins = 0;
        } else if (position == 3) {
            totalPoints += 1;
            consecutiveWins = 0;
        } else {
            consecutiveWins = 0;
        }
    }

    public String getBadge() {
        if (consecutiveWins >= 3) return "⚡ Speed Demon";
        if (totalBurnouts == 0 && racesPlayed >= 5) return "🔥 Iron Fingers";
        if (wins >= 3) return "🏆 Champion";
        if (racesPlayed >= 3) return "🎯 Veteran";
        return "🌱 Rookie";
    }

    public String getName() { return name; }
    public int getTotalPoints() { return totalPoints; }
    public int getWins() { return wins; }
    public int getRacesPlayed() { return racesPlayed; }
    public double getBestWPM() { return bestWPM; }
}