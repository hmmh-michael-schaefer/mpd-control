package de.sir4gt10.mpdcontrol.cover;

import de.sir4gt10.mpdcontrol.mpd.AlbumInfo;

public interface CoverDownloadListener 
{

    public void onCoverDownloaded(CoverInfo cover);
    public void onCoverDownloadStarted(CoverInfo cover);
    public void onCoverNotFound(CoverInfo coverInfo);
    public void tagAlbumCover(AlbumInfo albumInfo);

}
