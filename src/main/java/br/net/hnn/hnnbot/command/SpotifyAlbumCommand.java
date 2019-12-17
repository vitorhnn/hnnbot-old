package br.net.hnn.hnnbot.command;

import br.net.hnn.hnnbot.Bot;
import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class SpotifyAlbumCommand extends Command {
    public SpotifyAlbumCommand(Bot bot) {
        super(bot);

        this.name = "spotifyAlbum";
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        final var albumId = args[1];

        final var guild = event.getGuild();

        final var voiceChannel = this.getSenderVoiceChannel(event);

        if (voiceChannel == null) {
            return;
        }

        this.bot.getSpotifyAPIWrapper()
            .getAlbum(albumId)
            .thenApply(album -> album.tracks.items.stream()
                .map(track -> {
                        final AudioTrack[] topTrack = new AudioTrack[1];
                        try {
                            this.bot
                                .getGlobalMusicManager()
                                .getPlayerManager()
                                .loadItem("ytsearch:" + track.artists
                                        .get(0).name + " - " + track.name,
                                    new FunctionalResultHandler(
                                        ignored -> {
                                            throw new IllegalStateException("this should never load one track only");
                                        },
                                        playlist -> topTrack[0] = playlist.getTracks().get(0),
                                        null,
                                        null) {
                                    }).get();
                        } catch (InterruptedException | CancellationException | ExecutionException ignored) {
                        }

                        return topTrack[0];
                    }
                ).collect(Collectors.toUnmodifiableList())
            )
            .thenAccept(tracks -> {
                for (var track : tracks) {
                    this.bot.getGlobalMusicManager().loadTrack(guild, voiceChannel, track);
                }
            });
    }
}
