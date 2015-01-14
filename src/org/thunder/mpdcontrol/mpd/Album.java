
package org.thunder.mpdcontrol.mpd;

import org.thunder.mpdcontrol.R;

import android.os.Parcel;
import android.os.Parcelable;

public class Album extends Item implements Parcelable {
    public static String singleTrackFormat = "%d Track (%s)";
    public static String multipleTracksFormat = "%d Tracks (%s)";

    private final String name;
    private long songCount;
    private long duration;
    private long year;
    private String path;
    private Artist artist;
    private boolean hasAlbumArtist;

    public static final Parcelable.Creator<Album> CREATOR =
            new Parcelable.Creator<Album>() {
                public Album createFromParcel(Parcel in) {
                    return new Album(in);
                }

                public Album[] newArray(int size) {
                    return new Album[size];
                }
            };

    public Album(Album a) {
        this(a.name, a.songCount, a.duration, a.year,
                new Artist(a.artist), a.hasAlbumArtist, a.path);
    }

    protected Album(Parcel in) {
        this.name = in.readString();
        this.songCount = in.readLong();
        this.duration = in.readLong();
        this.year = in.readLong();
        this.path = in.readString();
        this.artist = new Artist(in.readString());
        this.hasAlbumArtist = (in.readInt() > 0);
    }

    public Album(String name, Artist artist) {
        this(name, 0, 0, 0, artist, false, "");
    }

    public Album(String name, Artist artist, boolean hasAlbumArtist) {
        this(name, 0, 0, 0, artist, hasAlbumArtist, "");
    }

    public Album(String name, Artist artist, boolean hasAlbumArtist, String path) {
        this(name, 0, 0, 0, artist, hasAlbumArtist, path);
    }

    public Album(String name, long songCount, long duration, long year, Artist artist,
            boolean hasAlbumArtist) {
        this(name, songCount, duration, year, artist, hasAlbumArtist, "");
    }

    public Album(String name, long songCount, long duration, long year,
            Artist artist, boolean hasAlbumArtist, String path) {
        this.name = name;
        this.songCount = songCount;
        this.duration = duration;
        this.year = year;
        this.artist = artist;
        this.hasAlbumArtist = hasAlbumArtist;
        this.path = path;
    }

    @Override
    public int compareTo(Item o) {
        if (o instanceof Album) {
            Album oa = (Album) o;
            if (MPD.sortAlbumsByYear()) {
                if (year != oa.year) {
                    return year < oa.year ? -1 : 1;
                }
            }
            //int comp = super.compareTo(o);
            //if (comp == 0 && artist != null) { // same album name, check artist
            //    comp = artist.compareTo(oa.artist);
            //}
        }
        return super.compareTo(o);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Album) {
            Album a = (Album) o;
            return (hasAlbumArtist == a.hasAlbumArtist &&
                    name.equals(a.getName()) && artist.equals(a.getArtist()));
        }
        return false;
    }

    public AlbumInfo getAlbumInfo() {
        return new AlbumInfo(this);
    }

    public Artist getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSongCount() {
        return songCount;
    }

    public long getYear() {
        return year;
    }

    public boolean hasAlbumArtist() {
        return hasAlbumArtist;
    }

    public String info() {
        return (artist == null ? "null" : artist.info()) +
                (hasAlbumArtist() ? " (AA)" : "") +
                " // " + name +
                ("".equals(path) ? "" : " (" + path + ")");
    }

    /*
     * text for display
     */
    @Override
    public String mainText() {
        return (name.equals("") ?
                MPD.getApplicationContext().getString(R.string.mpd_unknown_album) :
                name);
    }

    @Override
    public boolean nameEquals(Item o) {
        if (o instanceof Album) {
            Album a = (Album) o;
            return (name.equals(a.getName()) && artist.nameEquals(a.getArtist()));
        }
        return false;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public void setDuration(long d) {
        duration = d;
    }

    public void setHasAlbumArtist(boolean aa) {
        hasAlbumArtist = aa;
    }

    public void setPath(String p) {
        path = p;
    }

    public void setSongCount(long sc) {
        songCount = sc;
    }

    public void setYear(long y) {
        year = y;
    }

    @Override
    public String subText() {
        String construct = null;
        if (MPD.sortAlbumsByYear() && 0 != year) {
            construct = Long.toString(year);
        }
        if (0 != songCount) {
            if (construct != null)
                construct += " - ";
            construct += String.format(1 == songCount ? singleTrackFormat : multipleTracksFormat,
                    songCount, Music.timeToString(duration));
        }
        return construct;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.songCount);
        dest.writeLong(this.duration);
        dest.writeLong(this.year);
        dest.writeString(this.path);
        dest.writeString(this.artist == null ? "" : this.artist.getName());
        dest.writeInt(this.hasAlbumArtist() ? 1 : 0);
    }

}
