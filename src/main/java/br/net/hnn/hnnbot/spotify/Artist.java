package br.net.hnn.hnnbot.spotify;

public class Artist {
    /** The Spotify ID for the artist. **/
    public final String id;

    /** The name of the artist. **/
    public final String name;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
