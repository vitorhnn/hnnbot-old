package br.net.hnn.hnnbot.command;

import br.net.hnn.hnnbot.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SpotifyAlbumCommand extends Command {
    public SpotifyAlbumCommand(Bot bot) {
        super(bot);

        this.name = "spotifyAlbum";
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        final var albumId = args[1];

        this.bot.getSpotifyAPIWrapper().getAlbum(albumId).thenAccept(album -> {
            StringBuilder builder = new StringBuilder();
            builder.append("Artist is ")
                    .append(album.artists.get(0).name)
                    .append(", name is ")
                    .append(album.name)
                    .append("\nTracks are:");

            for (var track : album.tracks.items) {
                builder.append("\n").append(track.name);
            }

            event.getChannel().sendMessage(builder.toString()).queue();
        });
    }
}
