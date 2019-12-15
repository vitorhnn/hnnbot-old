package br.net.hnn.hnnbot.spotify;

import java.util.List;

public class Album {
    /** The artists of the album. **/
    public final List<Artist> artists;

    /** The name of the album. In case of an album takedown, the value may be an empty string. **/
    public final String name;

    /** The tracks of the album. **/
    public final Pager<Track> tracks;

    public Album(List<Artist> artists, String name, Pager<Track> tracks) {
        this.artists = artists;
        this.name = name;
        this.tracks = tracks;
    }
}
