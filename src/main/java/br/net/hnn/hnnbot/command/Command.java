package br.net.hnn.hnnbot.command;

import br.net.hnn.hnnbot.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Command executed in response to a {@link GuildMessageReceivedEvent}
 */
public abstract class Command {
    protected final Bot bot;

    /**
     * Command name, used when matching a message to a command (i.e {@code !command } matches a command with name command)
     */
    protected String name = "command";

    public Command(Bot bot) {
        this.bot = bot;
    }

    /**
     * Executes the command
     *
     * @param event The event that triggered this command.
     * @param args  Arguments passed to this command. {@code args[0]} is <b>always</b> the command's name as invoked
     */
    public abstract void execute(GuildMessageReceivedEvent event, String[] args);

    public final String getName() {
        return name;
    }
}
