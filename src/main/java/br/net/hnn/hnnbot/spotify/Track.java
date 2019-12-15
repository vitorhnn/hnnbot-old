package br.net.hnn.hnnbot.spotify;

import java.util.List;

public class Track {
    /** The artists who performed the track. Each artist object includes a link in href to more detailed information about the artist.
     *  hnn note: I get why this is here, but why is there an artist array on Album too?
     */
    public final List<Artist> artists;

    /** The name of the track. **/
    public final String name;

    public Track(List<Artist> artists, String name) {
        this.artists = artists;
        this.name = name;
    }
}
