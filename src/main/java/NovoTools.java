import java.util.Random;

public class NovoTools {
    // Used for various probabilities.
    private final Random random  = new Random();
    // This returns the number of successes made.
    public int[] startDifficultyRoll(String[] stats) {
        String difficulty;
        String stat;
        if (checkDifficultyValid(stats[0])) {
            difficulty = stats[0];
            stat = stats[1];
        }
        else {
            stat = stats[0];
            difficulty = stats[1];
        }
        System.out.println(difficulty + " " + stat);
        difficulty = new StringBuilder(difficulty).reverse().toString();
        System.out.println(difficulty + " " + stat);
        int[] statDice = getStatDice(new String[] {difficulty, stat});
        System.out.println(statDice[0] + " " + statDice[1]);
        return new int[] {rollDice(statDice)[0], difficulty.charAt(1) - '0'};
    }

    // Returns an array that shows the amount of successes each contestant got.
    public int[] statContest (String[] stats) throws Exception {
        for (String stat : stats) {
            if (!checkStatValid(stat)) {
                throw new Exception("Error: " + stat + " is not a valid stat.");
            }
        }

        int[] statsDice = getStatDice(stats);
        return rollDice(statsDice);
    }

    // Rolls a 50/50 chance of success for each element as many times as the element is, returning the successes.
    private int[] rollDice(int[] statsDice) {
        for (int i = 0, successes = 0; i < statsDice.length; i++) {
            for (int j = 0; j < statsDice[i]; j++) {
                if (random.nextInt(2) == 1)
                    successes++;
            }
            statsDice[i] = successes;
            successes = 0;
        }
        return statsDice;
    }

    // This function returns the relative dice to be rolled when comparing two dice.
    public int[] getStatDice(String[] stats) {
        int[] statsQuality = new int[stats.length];
        int[] statsQuantity = new int[stats.length];

        // Getting statsQuality. Using F as a base and making the number negative to reverse the ASCII order.
        // A = 5
        // B = 4
        // C = 3
        // D = 2
        // E = 1
        // F = 0
        for (int i = 0; i < statsQuantity.length; i++) {
            statsQuality[i] = (stats[i].charAt(0) - 'F') * -1;
        }

        // Getting statsQuantity.
        for (int i = 0; i < statsQuantity.length; i++) {
            statsQuantity[i] = stats[i].charAt(1) - '0';
        }

        // Finding the highest Quality
        int highestQuality = 0;
        for (int quality : statsQuality) {
            if (quality > highestQuality)
                highestQuality = quality;
        }

        int lowestQuality = highestQuality;
        for (int quality : statsQuality) {
            if (quality < lowestQuality)
                lowestQuality = quality;
        }

        // Subtracting the Highest Quality and adding the difference between the Stat's Quality and the Lowest Quality.
        for (int i = 0; i < statsQuantity.length; i++) {
            statsQuantity[i] += (statsQuality[i] - highestQuality) + (statsQuality[i] - lowestQuality);
            if (statsQuantity[i] < 0)
                statsQuantity[i] = 0;
        }

        return statsQuantity;
    }

    // Checks if the provided difficulty is valid or not.
    public boolean checkStatValid (String stat) {
        if (stat.equals(""))
            return false;
        return stat.length() <= 2 && stat.charAt(0) - 'A' <= 5 && stat.charAt(0) - 'A' >= 0 && stat.charAt(1) - '0' <= 6 && stat.charAt(1) - '0' >= 0;
    }

    // Checks if the provided stat is valid or not.
    public boolean checkDifficultyValid(String stat) {
        if (stat.equals(""))
            return false;
        return stat.length() <= 2 && stat.charAt(1) - 'A' <= 5 && stat.charAt(1) - 'A' >= 0 && stat.charAt(0) - '0' <= 6 && stat.charAt(0) - '0' >= 0;
    }
}
