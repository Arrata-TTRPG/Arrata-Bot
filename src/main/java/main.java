import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Locale;

public class main extends ListenerAdapter {
    private String[] keywords = {
            "based", "i love", "now this is", "i hate you"
            };
    private String[] responses = {
            "and redpilled.", "Same tbh.", "You bet your ass it is.", ":)"
            };

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
            if(msg.getContentRaw().substring(0, 2).equals("!A")) {
                event.getChannel().sendMessage(parseInput(msg.getContentRaw())).queue();
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
        try {
            String contentPruned = contentRaw.substring(contentRaw.indexOf(" "));
        } catch (Exception e) {
            return "Error: Proper format is \"!A Arguments\"";
        }
        return "Wow thanks for doing literally nothing.";
    }
}
