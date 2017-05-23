package de.sir4gt10.mpdcontrol.cover;

import de.sir4gt10.mpdcontrol.mpd.AlbumInfo;

public interface ICoverRetriever 
{

    public String[] getCoverUrl(AlbumInfo albumInfo) throws Exception;
    public String getName();
    public boolean isCoverLocal();
    
}
