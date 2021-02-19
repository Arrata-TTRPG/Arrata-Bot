import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Locale;

public class main extends ListenerAdapter {
    private final String[] keywords = {
            "based", "i love", "now this is", "i hate you", "thanks"
            };
    private final String[] responses = {
            "and redpilled.", "Same tbh.", "You bet your ass it is.", ":)", ":)"
            };
    private final NovoTools tools = new NovoTools();

    public static void main(String[] args) throws LoginException
    {
        if (args.length < 1) {
            System.out.println("You have to provide a token as first argument!");
            System.exit(1);
        }
        // args[0] should be the token
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
        JDABuilder.createLight(args[0], GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new main())
                .setActivity(Activity.playing("Arrata; Use \"!A\""))
                .build();
    }

    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        if (msg.getContentRaw().contains("!A")) {
            if(msg.getContentRaw().startsWith("!A")) {
                event.getChannel().sendMessage(parseInput(msg.getContentRaw().toUpperCase(Locale.ROOT))).queue();
            }
        }
        else {
            for (int i = 0; i < keywords.length; i++) {
                if (msg.getContentRaw().toLowerCase(Locale.ROOT).contains(keywords[i]))
                    event.getChannel().sendMessage(responses[i]).queue();
            }
        }
    }

    private String parseInput(String contentRaw) {
        if (contentRaw.equals("!A"))
            return "Wow thanks for doing literally nothing.";
        String[] stats;
        try {
            String contentPruned = contentRaw.substring(contentRaw.indexOf(" ") + 1);
            stats = contentPruned.split(" ");
        } catch (Exception e) {
            return "Error: Proper format is \"!A argument1 argument2\". Remember that stats/difficulties cannot be greater than A and 6.";
        }

        String difficulty = "";

        for (String stat : stats) {
            if (!tools.checkStatValid(stat)){
                if (!tools.checkDifficultyValid(stat)) {
                    return "Error: " + stat + " is not a proper stat/difficulty.";
                }
                difficulty = stat;
            }
        }

        if (stats.length == 1 && tools.checkStatValid(stats[0]))
            return "I can't really do anything with a single stat.";
        if (stats.length == 1 && tools.checkDifficultyValid(stats[0]))
            return "I can't really do anything with a single difficulty.";

        int[] results;
        if (stats.length == 2 && !difficulty.equals("")) {
            results = tools.startDifficultyRoll(stats);
            String output;
            if (results[0] < results[1])
                output = "you failed by " + (results[1] - results[0]) + " successes.";
            else
                output = "you succeeded by " + (results[0] - results[1]) + " successes.";
            return "You got " + results[0] + " successes against a " + difficulty + "; " + output;
        }
        else {
            StringBuilder output = new StringBuilder();
            try {
                results = tools.statContest(stats);
                for (int i = 1; i < results.length + 1; i++)
                    output.append("Contestant ").append(i).append(" got ").append(results[i - 1]).append(" successes.\n");
                return output.toString();
            } catch (Exception e) {
                return ("Error: Stats provided messed up. Do better next time.");
            }
        }
    }
}
