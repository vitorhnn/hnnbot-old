package br.net.hnn.hnnbot.command;

import br.net.hnn.hnnbot.Bot;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Command executed in response to a {@link GuildMessageReceivedEvent}
 */
public abstract class Command {
    protected final Bot bot;

    /**
     * Command name, used when matching a message to a command (i.e {@code
     * !command } matches a command with name command)
     */
    protected String name = "command";

    public Command(Bot bot) {
        this.bot = bot;
    }

    /**
     * Executes the command
     *
     * @param event The event that triggered this command.
     * @param args  Arguments passed to this command. {@code args[0]} is
     *              <b>always</b> the command's name as invoked
     */
    public abstract void execute(GuildMessageReceivedEvent event,
                                 String[] args);

    @Nullable
    public final VoiceChannel getSenderVoiceChannel(GuildMessageReceivedEvent event) {
        final var member = event.getMember();

        if (member == null) {
            return null;
        }

        final var voiceState = member.getVoiceState();

        if (voiceState == null) {
            return null;
        }

        return voiceState.getChannel();
    }

    public final String getName() {
        return name;
    }

}
