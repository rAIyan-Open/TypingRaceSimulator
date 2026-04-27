/**
 * Represents a competitor in a typing race.
 * Encapsulates all state: progress, accuracy, burnout.
 */
public class Typist {

    // --- Fields (private for encapsulation) ---
    private String name;
    private char symbol;
    private int progress;
    private boolean burntOut;
    private int burnoutTurnsRemaining;
    private double accuracy;

    // --- Constructor ---
    /**
     * Creates a new Typist with the given symbol, name, and accuracy.
     * Accuracy is clamped to [0.0, 1.0].
     */
    public Typist(char typistSymbol, String typistName, double typistAccuracy) {
        this.symbol = typistSymbol;
        this.name = typistName;
        this.progress = 0;
        this.burntOut = false;
        this.burnoutTurnsRemaining = 0;
        // Use setAccuracy to enforce clamping from the start
        setAccuracy(typistAccuracy);
    }

    // --- Mutators ---

    /**
     * Sets the typist into a burnt-out state lasting the given number of turns.
     */
    public void burnOut(int turns) {
        this.burntOut = true;
        this.burnoutTurnsRemaining = turns;
    }

    /**
     * Reduces burnout counter by one. Clears burnout state when it reaches zero.
     */
    public void recoverFromBurnout() {
        if (burnoutTurnsRemaining > 0) {
            burnoutTurnsRemaining--;
        }
        if (burnoutTurnsRemaining == 0) {
            burntOut = false;
        }
    }

    /**
     * Resets progress to zero and clears all burnout state. Ready for a new race.
     */
    public void resetToStart() {
        this.progress = 0;
        this.burntOut = false;
        this.burnoutTurnsRemaining = 0;
    }

    /**
     * Advances the typist forward by one character.
     */
    public void typeCharacter() {
        this.progress++;
    }

    /**
     * Moves the typist backwards by the given amount. Progress cannot go below zero.
     */
    public void slideBack(int amount) {
        this.progress -= amount;
        if (this.progress < 0) {
            this.progress = 0;
        }
    }

    /**
     * Sets accuracy, clamped to the range [0.0, 1.0].
     */
    public void setAccuracy(double newAccuracy) {
        if (newAccuracy < 0.0) {
            this.accuracy = 0.0;
        } else if (newAccuracy > 1.0) {
            this.accuracy = 1.0;
        } else {
            this.accuracy = newAccuracy;
        }
    }

    /**
     * Sets the character used to represent the typist.
     */
    public void setSymbol(char newSymbol) {
        this.symbol = newSymbol;
    }

    // --- Accessors ---

    /** Returns the typist's accuracy rating. */
    public double getAccuracy() {
        return accuracy;
    }

    /** Returns the typist's current progress. */
    public int getProgress() {
        return progress;
    }

    /** Returns the name of the typist. */
    public String getName() {
        return name;
    }

    /** Returns the character used to represent the typist. */
    public char getSymbol() {
        return symbol;
    }

    /** Returns burnout turns remaining (0 if not burnt out). */
    public int getBurnoutTurnsRemaining() {
        return burnoutTurnsRemaining;
    }

    /** Returns true if the typist is currently burnt out. */
    public boolean isBurntOut() {
        return burntOut;
    }
}