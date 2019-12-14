package br.net.hnn.hnnbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles all GuildMusicManagers and responds to messages
 */
public class GlobalMusicManager {
    private final AudioPlayerManager playerManager;

    private final Map<Long, GuildMusicManager> musicManagers;

    public GlobalMusicManager() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();

        AudioSourceManagers.registerLocalSource(playerManager);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    @NotNull
    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        final var guildId = Long.parseLong(guild.getId());

        final var musicManager = musicManagers.computeIfAbsent(guildId, k -> new GuildMusicManager(this.playerManager));

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(final Guild guild, final VoiceChannel voiceChannel, final String trackUrl, AudioLoadResultHandler audioLoadResultHandler) {
        final var musicManager = getGuildAudioPlayer(guild);

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                audioLoadResultHandler.trackLoaded(track);

                connectToVoiceChannel(guild, voiceChannel);
                enqueueTrack(musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                audioLoadResultHandler.playlistLoaded(playlist);


                connectToVoiceChannel(guild, voiceChannel);
                if (playlist.isSearchResult()) {
                    // we're just enqueing the first search result, which may not be a great idea
                    enqueueTrack(musicManager, playlist.getTracks().get(0));
                } else {
                    for (var track : playlist.getTracks()) {
                        enqueueTrack(musicManager, track);
                    }
                }
            }

            @Override
            public void noMatches() {
            }

            @Override
            public void loadFailed(FriendlyException exception) {
            }
        });
    }

    private void connectToVoiceChannel(Guild guild, VoiceChannel channel) {
        guild.getAudioManager().openAudioConnection(channel);
    }

    private void enqueueTrack(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.queue(track);
    }

    public void skipTrack(final Guild guild) {
        final var musicManager = getGuildAudioPlayer(guild);
        musicManager.scheduler.nextTrack();
    }
}
