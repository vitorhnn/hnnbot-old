package br.net.hnn.hnnbot;

import br.net.hnn.hnnbot.command.PlayCommand;
import br.net.hnn.hnnbot.command.SpotifyAlbumCommand;
import br.net.hnn.hnnbot.spotify.SpotifyAPIWrapper;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception {
        final var dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

        final var bot = new Bot(
            new SpotifyAPIWrapper(
                dotenv.get("SPOTIFY_ID"),
                dotenv.get("SPOTIFY_SECRET")
            )
        );

        bot.registerCommand(new PlayCommand(bot));
        bot.registerCommand(new SpotifyAlbumCommand(bot));

        new JDABuilder(AccountType.BOT)
            .setToken(dotenv.get("BOT_TOKEN"))
            .addEventListeners(bot)
            .setActivity(Activity.playing(Objects.requireNonNull(dotenv.get("ACTIVITY_PLAYING"))))
            .build();
    }
}
