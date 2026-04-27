import java.util.concurrent.TimeUnit;

/**
 * A typing race simulation. Three typists race to complete a passage of text,
 * advancing character by character — or sliding backwards when they mistype.
 *
 * Originally written by Ty Posaurus. Bugs fixed by Rayan.
 *
 * @author TyPosaurus (fixed by Rayan)
 * @version 1.0
 */
public class TypingRace {

    private int passageLength;
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int SLIDE_BACK_AMOUNT = 2;
    private static final int BURNOUT_DURATION = 3;

    /**
     * Constructor for TypingRace.
     * @param passageLength the number of characters in the passage
     */
    public TypingRace(int passageLength) {
        this.passageLength = passageLength;
        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;
    }

    /**
     * Seats a typist at the given seat number (1, 2, or 3).
     */
    public void addTypist(Typist theTypist, int seatNumber) {
        if (seatNumber == 1) {
            seat1Typist = theTypist;
        } else if (seatNumber == 2) {
            seat2Typist = theTypist;
        } else if (seatNumber == 3) {
            seat3Typist = theTypist;
        } else {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    /**
     * Starts the typing race.
     * BUG FIX 2: Added seat3Typist.resetToStart() — Ty forgot the third typist.
     * BUG FIX 4: Added winner announcement after the race.
     * BUG FIX 5: Winner accuracy increases; burnout decreases accuracy.
     * BUG FIX 7: Added null checks before accessing typists.
     */
    public void startRace() {
        // BUG FIX 7: Guard against unseated typists
        if (seat1Typist == null || seat2Typist == null || seat3Typist == null) {
            System.out.println("Error: All three seats must be filled before starting the race.");
            return;
        }

        boolean finished = false;

        // BUG FIX 2: seat3Typist was missing from the reset — added here
        seat1Typist.resetToStart();
        seat2Typist.resetToStart();
        seat3Typist.resetToStart();

        while (!finished) {
            advanceTypist(seat1Typist);
            advanceTypist(seat2Typist);
            advanceTypist(seat3Typist);

            printRace();

            if (raceFinishedBy(seat1Typist) || raceFinishedBy(seat2Typist) || raceFinishedBy(seat3Typist)) {
                finished = true;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        // BUG FIX 4: Announce the winner
        Typist winner = null;
        if (raceFinishedBy(seat1Typist)) {
            winner = seat1Typist;
        } else if (raceFinishedBy(seat2Typist)) {
            winner = seat2Typist;
        } else {
            winner = seat3Typist;
        }

        // BUG FIX 5: Winning increases accuracy slightly
        double oldAccuracy = winner.getAccuracy();
        winner.setAccuracy(oldAccuracy + 0.02);
        double newAccuracy = Math.round(winner.getAccuracy() * 100.0) / 100.0;
        double oldAccuracyRounded = Math.round(oldAccuracy * 100.0) / 100.0;

        System.out.println("\n And the winner is... " + winner.getName() + "!");
        System.out.println(" Final accuracy: " + newAccuracy + " (improved from " + oldAccuracyRounded + ")");

    }

    /**
     * Simulates one turn for a typist.
     * BUG FIX 3: Mistype chance now correctly decreases for higher accuracy typists.
     *            Was: Math.random() < accuracy * MISTYPE_BASE_CHANCE  (inverted — high accuracy = more mistypes)
     *            Fix: Math.random() < (1 - accuracy) * MISTYPE_BASE_CHANCE (correct — high accuracy = fewer mistypes)
     * BUG FIX 5: Burnout now decreases accuracy slightly.
     */
    private void advanceTypist(Typist theTypist) {
        if (theTypist.isBurntOut()) {
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy()) {
            theTypist.typeCharacter();
        }

        // BUG FIX 3: Mistype chance should be LOWER for higher accuracy typists
        // Old (buggy): Math.random() < theTypist.getAccuracy() * MISTYPE_BASE_CHANCE
        // Fixed: use (1 - accuracy) so higher accuracy = lower mistype chance
        if (Math.random() < (1 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE) {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
        }

        // Burnout check — pushing too hard increases burnout risk for high-accuracy typists
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy()) {
            // BUG FIX 5: Burnout decreases accuracy slightly
            theTypist.setAccuracy(theTypist.getAccuracy() - 0.01);
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }

    /**
     * Returns true if the given typist has finished the race.
     * BUG FIX 1: Changed == to >= to handle progress overshooting passage length.
     *            Was: theTypist.getProgress() == passageLength  (infinite loop if overshot)
     *            Fix: theTypist.getProgress() >= passageLength
     */
    private boolean raceFinishedBy(Typist theTypist) {
        // BUG FIX 1: must use >= not == because progress can overshoot
        return theTypist.getProgress() >= passageLength;
    }

    /**
     * Prints the current state of the race to the terminal.
     * BUG FIX 6: Fixed legend — was [zz] but code uses ~ for burnout symbol.
     */
    private void printRace() {
        System.out.print('\u000C');

        System.out.println(" TYPING RACE - passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist);
        System.out.println();

        printSeat(seat2Typist);
        System.out.println();

        printSeat(seat3Typist);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        // BUG FIX 6: Legend updated to match ~ symbol used in printSeat
        System.out.println(" [~] = burnt out    [<] = just mistyped");
    }

    /**
     * Prints a single typist's lane.
     */
    private void printSeat(Typist theTypist) {
        int spacesBefore = theTypist.getProgress();
        int spacesAfter = passageLength - theTypist.getProgress();

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut()) {
            System.out.print('~');
            spacesAfter--;
        }

        if (spacesAfter > 0) {
            multiplePrint(' ', spacesAfter);
        }

        System.out.print('|');
        System.out.print(' ');

        if (theTypist.isBurntOut()) {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        } else {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")");
        }
    }

    /**
     * Prints a character a given number of times.
     */
    private void multiplePrint(char aChar, int times) {
        int i = 0;
        while (i < times) {
            System.out.print(aChar);
            i = i + 1;
        }
    }
}