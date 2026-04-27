public class TypistTest {
    public static void main(String[] args) {

        // Test 1: slideBack cannot go below zero
        System.out.println("=== Test 1: slideBack cannot go below zero ===");
        Typist t1 = new Typist('①', "TURBOFINGERS", 0.85);
        t1.typeCharacter();
        t1.slideBack(5);
        System.out.println("Expected: 0 | Got: " + t1.getProgress());

        // Test 2: burnout countdown
        System.out.println("\n=== Test 2: Burnout counts down and clears ===");
        Typist t2 = new Typist('②', "QWERTY_QUEEN", 0.60);
        t2.burnOut(3);
        System.out.println("Burnt out: " + t2.isBurntOut() + " | Turns remaining: " + t2.getBurnoutTurnsRemaining());
        t2.recoverFromBurnout();
        System.out.println("After 1 recover: " + t2.getBurnoutTurnsRemaining());
        t2.recoverFromBurnout();
        System.out.println("After 2 recover: " + t2.getBurnoutTurnsRemaining());
        t2.recoverFromBurnout();
        System.out.println("After 3 recover - Burnt out: " + t2.isBurntOut() + " | Turns: " + t2.getBurnoutTurnsRemaining());

        // Test 3: resetToStart clears everything
        System.out.println("\n=== Test 3: resetToStart clears all state ===");
        Typist t3 = new Typist('③', "HUNT_N_PECK", 0.30);
        t3.typeCharacter(); t3.typeCharacter(); t3.typeCharacter();
        t3.burnOut(2);
        System.out.println("Before reset - Progress: " + t3.getProgress() + " | Burnt out: " + t3.isBurntOut());
        t3.resetToStart();
        System.out.println("After reset  - Progress: " + t3.getProgress() + " | Burnt out: " + t3.isBurntOut() + " | Turns: " + t3.getBurnoutTurnsRemaining());

        // Test 4: accuracy clamping
        System.out.println("\n=== Test 4: Accuracy clamped to [0.0, 1.0] ===");
        Typist t4 = new Typist('④', "SPEEDY", 0.5);
        t4.setAccuracy(1.5);
        System.out.println("setAccuracy(1.5) → Expected: 1.0 | Got: " + t4.getAccuracy());
        t4.setAccuracy(-0.3);
        System.out.println("setAccuracy(-0.3) → Expected: 0.0 | Got: " + t4.getAccuracy());
        t4.setAccuracy(0.75);
        System.out.println("setAccuracy(0.75) → Expected: 0.75 | Got: " + t4.getAccuracy());

        // Test 5: normal typeCharacter movement
        System.out.println("\n=== Test 5: Normal forward movement via typeCharacter ===");
        Typist t5 = new Typist('⑤', "CLICKMASTER", 0.70);
        System.out.println("Start: " + t5.getProgress());
        t5.typeCharacter(); t5.typeCharacter(); t5.typeCharacter();
        System.out.println("After 3 typeCharacter calls - Expected: 3 | Got: " + t5.getProgress());

        System.out.println("\nAll tests complete.");
    }
}