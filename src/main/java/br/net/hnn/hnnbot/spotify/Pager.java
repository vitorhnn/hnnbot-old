package br.net.hnn.hnnbot.spotify;

import java.util.List;

/** I don't really care about this class right now. It'll probably be important soon **/
public class Pager<T> {
    public final List<T> items;

    public Pager(List<T> items) {
        this.items = items;
    }
}
