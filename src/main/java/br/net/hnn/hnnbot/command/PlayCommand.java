package br.net.hnn.hnnbot.command;

import br.net.hnn.hnnbot.Bot;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PlayCommand extends Command {
    public PlayCommand(Bot bot) {
        super(bot);

        this.name = "play";
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, String[] args) {
        final var guild = event.getGuild();
        final var voiceChannel = this.getSenderVoiceChannel(event);

        if (voiceChannel == null) {
            return;
        }

        bot.getGlobalMusicManager()
            .loadAndPlay(guild, voiceChannel, args[1],
                new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        event.getChannel()
                            .sendMessage("Loaded track: " + track.getInfo().title)
                            .queue();
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {

                    }

                    @Override
                    public void noMatches() {
                        event.getChannel().sendMessage("No tracks found").queue();
                    }

                    @Override
                    public void loadFailed(FriendlyException exception) {

                    }
                });
    }
}
