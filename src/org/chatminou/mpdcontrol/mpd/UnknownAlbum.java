
package org.chatminou.mpdcontrol.mpd;

import org.chatminou.mpdcontrol.R;

import android.os.Parcel;
import android.os.Parcelable;

public class UnknownAlbum extends Album {
    public static final UnknownAlbum instance = new UnknownAlbum();

    public static final Parcelable.Creator<UnknownAlbum> CREATOR =
            new Parcelable.Creator<UnknownAlbum>() {
                public UnknownAlbum createFromParcel(Parcel in) {
                    return new UnknownAlbum(in);
                }

                public UnknownAlbum[] newArray(int size) {
                    return new UnknownAlbum[size];
                }
            };

    private UnknownAlbum() {
        super(MPD.getApplicationContext().getString(R.string.mpd_unknown_album),
                UnknownArtist.instance);
    }

    protected UnknownAlbum(Parcel in) {
        super(in);
    }

    @Override
    public String subText() {
        return "";
    }

}
