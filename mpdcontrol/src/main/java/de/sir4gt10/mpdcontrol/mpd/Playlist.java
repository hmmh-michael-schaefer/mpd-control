
package de.sir4gt10.mpdcontrol.mpd;

public class Playlist extends Item {
    private final String name;

    public Playlist(String name) {
        this.name = name;
    }

    public String displayName() {
        return name;
    }

    public String getName() {
        return name;
    }
}
