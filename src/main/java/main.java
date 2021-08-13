import com.vdurmont.emoji.EmojiParser;
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
            "based", "i love", "now this is", "i hate", "thanks", "hewwo", "crack", "war crime", "xd", "lol", "kill me", "floop", "amogus", "bork", "compliment me", "pog", "poggers", "(:", "{=", "genius"
    };
    private final String[] responses = {
            "and redpilled.", "Same tbh.", "You bet your ass it is.", ":)", ":)", "\"hewwo\" owo die", "*I love crack*.\n:)", "Very funny, funny man.",
            "XD RAWR LOL", "Commit die.", "I wish I could.\n:)", "Wow how original and quirky.",
            "GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD GET OUT OF MY HEAD",
            "Dogs are not allowed. Unless you're ordering.", "You're a malformed brainlet.", "POG?!", "POGGERS?!", "It's :) retard.\n:)", "It's :) retard.\n:)", "Ur mom is a genius."
    };
    private final String[] banned = {
            "rawr", "kill yourself", "fuck", "damn", "shit", "cunt", "ass", "dick", "cock", "uwu", "owo",
            "I love based crack. Thanks for the war crimes xd lol rawr kill me tho. Haha amogus floop, bork bork, please compliment me for my pog poggers (: {=", "ⱼₑᵥᵥ", "ₒwₒ", "ᵤwᵤ", "cringe"
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

    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        if (msg.getAuthor().isBot())
            return;
        System.out.println(msg.getContentRaw() + " " + event.getAuthor().getId());
        if (msg.getContentRaw().contains("!A")) {
            if(msg.getContentRaw().startsWith("!A")) {
                event.getChannel().sendMessage(parseInput(msg.getContentRaw().toUpperCase(Locale.ROOT))).queue();
            }
        }
        else {
            try {
                if (hasEmoji(msg.getContentRaw().toUpperCase(Locale.ROOT))) {
                    event.getChannel().sendMessage("Did you just use an emoji?\nWhat the fuck is wrong with you?\n:(").queue();
                    event.getMessage().delete().queue();
                    return;
                }
            } catch (Exception ignored) {}
            for (String s : banned) {
                if (msg.getContentRaw().toLowerCase(Locale.ROOT).contains(s) &&
                        !event.getAuthor().getId().equals("266478982294274048")) {
                    event.getChannel().sendMessage("You just said an uh oh bad stinky word. I deleted it so that no one else has to suffer your cringe.\n:)").queue();
                    event.getMessage().delete().queue();
                    return;
                }
            }
            for (int i = 0; i < keywords.length; i++) {
                if (msg.getContentRaw().toLowerCase(Locale.ROOT).contains(keywords[i])) {
                    event.getChannel().sendMessage(responses[i]).queue();
                    return;
                }
            }
        }
    }

    private boolean hasEmoji(String message) {
        return EmojiParser.extractEmojis(message).size() > 0;
    }

    private String parseInput(String contentRaw) {
        if (contentRaw.equals("!A"))
            return "Wow thanks for doing literally nothing.";
        String[] stats;
        try {
            // Gets rid of the "!A".
            String contentPruned = contentRaw.substring(contentRaw.indexOf(" ") + 1);
            // Splits the input.
            stats = contentPruned.split(" ");
        } catch (Exception e) {
            // If the format is wrong, we make fun of them.
            return "Error: Proper format is \"!A argument1 argument2\".";
        }

        if (stats.length > 2)
            return "Error: More than two arguments provided.";

        if (stats[0].equals("CHAR")) {
            if (stats.length > 1) {
                return tools.createCharacter(Integer.parseInt(stats[1]));
            }
            else return tools.createCharacter(1);
        }

        if (stats.length == 1 && tools.checkStatValid(stats[0])) {
            String[] successes = tools.roll(stats[0]);
            return "Rolling **" + stats[0] + "**.\n" + "Results: " + successes[0] + "\n**" + successes[1] + "** success(es)";
        }

        if (stats.length == 2 && tools.checkStatValid(stats[0]) && tools.checkObValid(stats[1])) {
            String[] successes = tools.roll(stats[0]);
            String result = "Rolling **" + stats[0] + "** vs **Ob " + stats[1] + "**\n" + "Results: " + successes[0] + "\n**" + successes[1] + "** success(es)\n";
            if (Integer.parseInt(successes[1]) < Integer.parseInt(stats[1])) {
                return result + "**Failure**";
            }
            else
                return result + "**Success!**";
        }

        if (stats.length == 2 && tools.checkStatValid(stats[1]) && tools.checkObValid(stats[0])) {
            String[] successes = tools.roll(stats[1]);
            String result = "Rolling **" + stats[1] + "** vs **Ob " + stats[0] + "**\n" + "Results: " + successes[0] + "\n**" + successes[1] + "** success(es)\n";
            if (Integer.parseInt(successes[1]) < Integer.parseInt(stats[0])) {
                return result + "**Failure**";
            }
            else
                return result + "**Success!**";
        }
        return "Error: Parsing Error.";
    }
}
