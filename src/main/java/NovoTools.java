import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class NovoTools {
    // Used for various probabilities.
    private final Random random  = new Random();



    // Checks if the provided difficulty is valid or not.
    public boolean checkStatValid (String stat) {
        if (stat.equals(""))
            return false;
        int Quality = 0;
        switch (stat.charAt(0)) {
            case 'W':
            case 'G':
            case 'B':
                break;
            default:
                return false;
        }
        try {
            if (Integer.parseInt(stat.substring(1)) > 300)
                return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // Returns the number of successes rolled.
    public String[] roll(String stat) {
        int[] statComponents = getStatComponents(stat);
        String[] values = new String[] {"",""};
        int successes = 0;
        int value;
        for (int i = 0; i < statComponents[1]; i++) {
            value = random.nextInt(6) + 1;
            if (value >= statComponents[0]) {
                successes++;
                values[0] += "**" + value + "** ";
            }
            else {
                values[0] += value + " ";
            }
        }

        values[1] = Integer.toString(successes);
        return values;
    }

    private int[] getStatComponents(String stat) {
        int[] statComponents = new int[2];
        switch (stat.charAt(0)) {
            case 'W':
                statComponents[0] = 2;
                break;
            case 'G':
                statComponents[0] = 3;
                break;
            case 'B':
                statComponents[0] = 4;
                break;
        }
        statComponents[1] = Integer.parseInt(stat.substring(1));
        return statComponents;
    }

    // A quick function to determine if an Ob is a number.
    public boolean checkObValid(String Ob) {
        try {
            Integer.parseInt(Ob);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String createCharacter(int level) {
        String character = "";
        if (level < 1)
            return "Error: Level must be 1 or higher.";
        else if (level > 300)
            return "Error: Level cannot be higher than 300.";
        // For the sake of simplicity, we decrement the level.
        level--;

        // This section declares the various variables a character has.
        ArrayList<String> ethosQuirks = getQuirks("ethos");
        ArrayList<String> pathosQuirks = getQuirks("pathos");
        ArrayList<String> logosQuirks = getQuirks("logos");

        String ethos = ethosQuirks.get(random.nextInt(ethosQuirks.size()));
        String pathos = pathosQuirks.get(random.nextInt(pathosQuirks.size()));
        String logos = logosQuirks.get(random.nextInt(logosQuirks.size()));

        String[] statsArray = new String[6];
        int[] statsArrayInts = new int[] {1, 1, 1, 1, 1, 1};
        ArrayList<String> skills = getSkillList();
        if (skills.size() == 0) {
            return "Error: No skills loaded.";
        }

        // Every level-up grants an additional stat point.
        // Every 2 level-ups grants another stat point.
        int statPool = 18 + level + (level / 2);
        int bound = 8 + level / 2;

        // These two loops set up the character's stats.
        while (statPool != 0) {
            for (int i = 0; i < statsArray.length; i++) {
                if (statPool == 0)
                    break;
                if (random.nextInt(2) == 1) {
                    statsArrayInts[i]++;
                    statPool--;
                }
            }
        }

        for (int i = 0; i < statsArray.length; i++) {
            if (statsArrayInts[i] > 5 && random.nextInt(4) == 0 || statsArrayInts[i] > 10) {
                statsArrayInts[i] -= 5;
                statsArray[i] = "G" + statsArrayInts[i];
                if (statsArrayInts[i] > 5 && random.nextInt(4) == 0 || statsArrayInts[i] > 10) {
                    statsArrayInts[i] -= 5;
                    statsArray[i] = "W" + statsArrayInts[i];
                }
            }
            else {
                statsArray[i] = "B" + statsArrayInts[i];
            }
        }

        // This array stores the values for each skill.
        // Every 3 level-ups provides a Minor skill, and every 5 provides a Major skill.
        int majors = 3 + ((level - 1) / 5);
        int minors = 5 + ((level - 1) / 3);
        String[] majorSkills = new String[majors];
        String[] minorSkills = new String[minors];

        if (ethos.contains("Blessed"))
            majorSkills[0] = generateSkillStat("Faith,2", 1, statsArray);
        if (logos.contains("Gifted")) {
            if (majorSkills[0] == null)
                majorSkills[0] = generateSkillStat("Sorcery,2", 1, statsArray);
            else
                majorSkills[1] = generateSkillStat("Sorcery,2", 1, statsArray);
        }
        skills.remove("Sorcery,2");
        skills.remove("Faith,2");

        for (int i = 0; i < majors && skills.size() != 0; i++) {
            if (!(majorSkills[i] == null))
                continue;
            int skillIndex = random.nextInt(skills.size());
            majorSkills[i] = generateSkillStat(skills.get(skillIndex), 1, statsArray);
            skills.remove(skillIndex);
        }

        for (int i = 0; i < minors && skills.size() != 0; i++) {
            int skillIndex = random.nextInt(skills.size());
            minorSkills[i] = generateSkillStat(skills.get(skillIndex), 0, statsArray);
            skills.remove(skillIndex);
        }

        character += "Name: " + randomName() + "\n" +
                     "Level: " + (level + 1) + "\n" +
                     "__**Quirks**__" + "\n" +
                     "Ethos: " + ethos + "\n" +
                     "Pathos: " + pathos + "\n" +
                     "Logos: " + logos + "\n" +
                     "__**Stats**__" + "\n" +
                     "Will: " + statsArray[0] + " Power: " + statsArray[3] + "\n" +
                     "Perception: " + statsArray[1] + " Speed: " + statsArray[4] + " Natural Armor: " + getNaturalArmor(statsArray[4]) + "\n" +
                     "Conscious: " + statsArray[2] + " Forte: " + statsArray[5] + " Health: " + getHealth(statsArray, level) + "\n" +
                     "__**Skills**__" + "\n" +
                     "__Majors__" + "\n";
        for (String major : majorSkills) {
            if (major == null)
                break;
            character += major + "\n";
        }

        character += "__Minors__" + "\n";
        for (String minor : minorSkills) {
            if (minor == null)
                break;
            character += minor + "\n";
        }
        return character;
    }

    private int getHealth(String[] statsArray, int level) {
        int willDice = Integer.parseInt(statsArray[0].substring(1));
        int forteDice = Integer.parseInt(statsArray[5].substring(1));

        int totalHealth = 5 + willDice + forteDice;

        totalHealth += ((willDice + forteDice) / 2.0) * level;

        return totalHealth;
    }

    private int getNaturalArmor(String speed) {
        switch(speed.charAt(0)) {
            case 'B':
                return (int) Math.round(Double.parseDouble(speed.substring(1)) / 2.0);
            case 'G':
                return Integer.parseInt(speed.substring(1));
            case 'W':
                return (int) Math.round(Double.parseDouble(speed.substring(1)) * 1.5);
        }
        return -1;
    }

    private ArrayList<String> getQuirks(String quirkName) {
        ArrayList<String> quirks = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("quirks/" + quirkName + ".txt"));
            String quirk;
            while ((quirk = reader.readLine()) != null) {
                quirks.add(quirk);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return quirks;
    }

    private String randomName() {
        ArrayList<String> names = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("names.txt"));
            String name;
            while ((name = reader.readLine()) != null) {
                names.add(name);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return names.get(random.nextInt(names.size())) + " " + names.get(random.nextInt(names.size()));
    }

    private ArrayList<String> getSkillList() {
        ArrayList<String> skills = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("skills.txt"));
            String skill;
            while ((skill = reader.readLine()) != null) {
                skills.add(skill);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return skills;
    }

    private String generateSkillStat(String skill, int majorMinor, String[] statsArray) {
        String[] skillComponents = skill.split(",");

        // Storing the name of the skill in returnString.
        String returnString = skillComponents[0] + " ";

        // If the skill has 2 root stats.
        if (skillComponents.length == 3) {
            // The average of the two root stats.
            int Quantity = (int)Math.round((Double.parseDouble(statsArray[Integer.parseInt(skillComponents[1])].substring(1)) +
                    Double.parseDouble(statsArray[Integer.parseInt(skillComponents[2])].substring(1))) / 2.0);

            // If the skill is minor, it starts at 2/3 of the root's Quantity.
            // 0 is minor, 1 is major.
            if (majorMinor == 0)
                Quantity = (int) Math.round(((double)Quantity * 2.0) / 3.0);
            // The higher Quality is chosen over the lower one.
            if (statsArray[Integer.parseInt(skillComponents[1])].charAt(0) > statsArray[Integer.parseInt(skillComponents[2])].charAt(0))
                returnString += statsArray[Integer.parseInt(skillComponents[1])].charAt(0);
            else
                returnString += statsArray[Integer.parseInt(skillComponents[2])].charAt(0);
            return returnString + Quantity;
        }
        // If the skill only has 1 root stat.
        else {
            // Minor skills start at 2/3 of the root stat.
            if (majorMinor == 0) {
                int Quantity = (int) Math.round(
                        ((Double.parseDouble(statsArray[Integer.parseInt(skillComponents[1])].substring(1))) * 2.0) /
                                3.0);
                return returnString + statsArray[Integer.parseInt(skillComponents[1])].charAt(0) + Quantity;
            }
        }
        return returnString + statsArray[Integer.parseInt(skillComponents[1])];
    }
}
