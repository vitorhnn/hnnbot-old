package br.net.hnn.hnnbot;

import br.net.hnn.hnnbot.command.Command;
import br.net.hnn.hnnbot.music.GlobalMusicManager;
import br.net.hnn.hnnbot.spotify.SpotifyAPIWrapper;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;

public class Bot extends ListenerAdapter {
    private final GlobalMusicManager globalMusicManager;

    private final SpotifyAPIWrapper spotifyAPIWrapper;

    private final HashMap<String, Command> commandMap;

    public Bot(SpotifyAPIWrapper spotifyAPIWrapper) {
        this.globalMusicManager = new GlobalMusicManager();
        this.commandMap = new HashMap<>();
        this.spotifyAPIWrapper = spotifyAPIWrapper;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] split = event.getMessage().getContentRaw().split(" ");

        if (split[0].startsWith("!")) {
            final var commandName = split[0].substring(1);

            final var maybeCommand = this.commandMap.get(commandName);

            if (maybeCommand != null) {
                maybeCommand.execute(event, split);
            }
        }

        super.onGuildMessageReceived(event);
    }

    public void registerCommand(Command command) {
        if (commandMap.get(command.getName()) != null) {
            throw new IllegalArgumentException("There already exists a command with name: " + command.getName());
        }

        commandMap.put(command.getName(), command);
    }

    public GlobalMusicManager getGlobalMusicManager() {
        return globalMusicManager;
    }

    public SpotifyAPIWrapper getSpotifyAPIWrapper() {
        return spotifyAPIWrapper;
    }
}
