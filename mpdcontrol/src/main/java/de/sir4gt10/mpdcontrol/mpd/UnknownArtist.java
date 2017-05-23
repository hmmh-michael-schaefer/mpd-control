
package de.sir4gt10.mpdcontrol.mpd;

import de.sir4gt10.mpdcontrol.R;

import android.os.Parcel;

public class UnknownArtist extends Artist {

    public static final UnknownArtist instance = new UnknownArtist();

    public static final Creator<UnknownArtist> CREATOR =
            new Creator<UnknownArtist>() {
                public UnknownArtist createFromParcel(Parcel in) {
                    return new UnknownArtist(in);
                }

                public UnknownArtist[] newArray(int size) {
                    return new UnknownArtist[size];
                }
            };

    private UnknownArtist() {
        super(MPD.getApplicationContext().getString(R.string.mpd_unknown_artist), 0);
    }

    protected UnknownArtist(Parcel in) {
        super(in);
    }

    @Override
    public String subText() {
        return "";
    }

}
