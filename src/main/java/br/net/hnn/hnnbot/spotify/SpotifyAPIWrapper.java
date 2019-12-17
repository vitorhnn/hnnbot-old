package br.net.hnn.hnnbot.spotify;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper around the Spotify API (https://developer.spotify
 * .com/documentation/web-api/). Extremely hacky and ad-hoc.
 * Based on Java 11's async HttpClient (maybe not the best of ideas) and Gson
 */
public class SpotifyAPIWrapper {
    private static final String SPOTIFY_AUTH_URL = "https://accounts.spotify.com";

    private static final String SPOTIFY_API_URL = "https://api.spotify.com";

    private final HttpClient httpClient;

    private final String spotifyId;

    private final String spotifySecret;
    private final Gson gson;
    private Optional<SpotifyToken> spotifyToken;

    public SpotifyAPIWrapper(String spotifyId, String spotifySecret) {
        this.httpClient = HttpClient.newHttpClient();

        this.gson = new Gson();
        this.spotifyToken = Optional.empty();

        this.spotifyId = spotifyId;
        this.spotifySecret = spotifySecret;
    }

    private CompletableFuture<SpotifyToken> refreshAccessToken() {
        final var basicAuthConcat = this.spotifyId + ":" + this.spotifySecret;
        final var base64Concat = Base64.getEncoder()
            .encodeToString(basicAuthConcat.getBytes());

        final var req = HttpRequest.newBuilder(URI.create(SPOTIFY_AUTH_URL + "/api/token"))
            .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic " + base64Concat)
            .build();

        return httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenApply(str -> this.gson.fromJson(str, ClientCredentialsResponse.class))
            .thenApply(clientCredentialsResponse -> {
                final var token =
                    new SpotifyToken(clientCredentialsResponse.accessToken, clientCredentialsResponse.expiresIn);
                this.spotifyToken = Optional.of(token);

                return token;
            });
    }

    /**
     * Refreshes the Spotify token if it is empty or if it has expired
     */
    private CompletableFuture<SpotifyToken> maybeRefreshToken() {
        // TODO: probably rewrite in a more functional fashion
        if (this.spotifyToken.isPresent()) {
            final var token = this.spotifyToken.get();

            if (token.expiresAt.isBefore(Instant.now())) {
                return this.refreshAccessToken();
            }

            return CompletableFuture.completedFuture(this.spotifyToken.get());
        }

        return this.refreshAccessToken();
    }

    public CompletableFuture<Album> getAlbum(String albumId) {
        return this.maybeRefreshToken()
            .thenCompose(token -> {
                final var req =
                    HttpRequest.newBuilder(URI.create(SPOTIFY_API_URL + "/v1/albums/" + albumId))
                        .GET()
                        .header("Authorization", "Bearer " + token.bearerToken)
                        .build();

                return httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString());
            })
            .thenApply(HttpResponse::body)
            .thenApply(str -> this.gson.fromJson(str, Album.class));
    }

    private static class ClientCredentialsResponse {
        @SerializedName("access_token")
        public String accessToken;

        @SerializedName("token_type")
        public String tokenType;

        @SerializedName("expires_in")
        public int expiresIn;
    }

    private static class SpotifyToken {
        public String bearerToken;

        public Instant expiresAt;

        public SpotifyToken(String bearerToken, int expiresIn) {
            this.bearerToken = bearerToken;
            this.expiresAt = Instant.now().plusSeconds(expiresIn);
        }
    }
}
