package de.sir4gt10.mpdcontrol.cover.provider;

import static android.text.TextUtils.isEmpty;
import android.content.SharedPreferences;
import android.net.Uri;

import de.sir4gt10.mpdcontrol.MainApplication;
import de.sir4gt10.mpdcontrol.cover.ICoverRetriever;
import de.sir4gt10.mpdcontrol.mpd.AlbumInfo;

import java.util.ArrayList;
import java.util.List;

public class LocalCover implements ICoverRetriever 
{

    private final static String URL_PREFIX = "http://";
    private final static String PLACEHOLDER_FILENAME = "%placeholder_filename";

    private final static String[] FILENAMES = new String[] 
    {
    	"%placeholder_custom", PLACEHOLDER_FILENAME, "folder", "cover", "front"
    };
    
    private final static String[] EXT = new String[] 
    {
    	"jpg", "png", "jpeg"
    };
    
    private final static String[] SUB_FOLDERS = new String[] 
    {
    	""//, "artwork", "Covers"
    };
    
    public static final String RETRIEVER_NAME = "User's HTTP Server";

    static public void appendPathString(Uri.Builder builder, String string) 
    {
        if (string != null && string.length() > 0) 
        {
            final String[] components = string.split("/");
            for (String component : components) 
            {
                builder.appendPath(component);
            }
        }
    }

    static public String buildCoverUrl(String serverName, String musicPath, String path, String fileName) 
    {

        if (musicPath.startsWith(URL_PREFIX)) 
        {
            int hostPortEnd = musicPath.indexOf(URL_PREFIX.length(), '/');
            if (hostPortEnd == -1) 
            {
                hostPortEnd = musicPath.length();
            }
            serverName = musicPath.substring(URL_PREFIX.length(), hostPortEnd);
            musicPath = musicPath.substring(hostPortEnd);
        }
        
        Uri.Builder b = Uri.parse(URL_PREFIX + serverName).buildUpon();
        appendPathString(b, musicPath);
        appendPathString(b, path);
        appendPathString(b, fileName);

        Uri uri = b.build();
        return uri.toString();
    }

    private MainApplication app = null;

    private SharedPreferences settings = null;

    public LocalCover(MainApplication app, SharedPreferences settings) 
    {
        this.app = app;
        this.settings = settings;
    }

    @Override
    public String[] getCoverUrl(AlbumInfo albumInfo) throws Exception 
    {

        if (isEmpty(albumInfo.getPath())) 
        {
            return new String[0];
        }

        String lfilename;
        // load URL parts from settings
        String musicPath = settings.getString("musicPath", "mpd/");
        FILENAMES[0] = settings.getString("coverFileName", null);

        if (musicPath != null) 
        {
            // load server name/ip
            final String serverName = app.oMPDAsyncHelper.getConnectionSettings().sServer;

            String url;
            final List<String> urls = new ArrayList<String>();
            for (String subfolder : SUB_FOLDERS) 
            {
                for (String baseFilename : FILENAMES) 
                {
                    for (String ext : EXT) 
                    {
                        if (baseFilename == null
                                || (baseFilename.startsWith("%") && !baseFilename
                                        .equals(PLACEHOLDER_FILENAME)))
                            continue;
                        if (baseFilename.equals(PLACEHOLDER_FILENAME)
                                && albumInfo.getFilename() != null) 
                        {
                            final int dotIndex = albumInfo.getFilename().lastIndexOf('.');
                            if (dotIndex == -1)
                                continue;
                            baseFilename = albumInfo.getFilename().substring(0, dotIndex);
                        }

                        // Add file extension except for the filename coming
                        // from settings
                        if (!baseFilename.equals(FILENAMES[0])) 
                        {
                            lfilename = subfolder + "/" + baseFilename + "." + ext;
                        } 
                        else 
                        {
                            lfilename = baseFilename;
                        }

                        url = buildCoverUrl(serverName, musicPath, albumInfo.getPath(), lfilename);

                        if (!urls.contains(url))
                            urls.add(url);
                    }
                }
            }
            return urls.toArray(new String[urls.size()]);
        } 
        else 
        {
            return null;
        }
    }

    @Override
    public String getName() 
    {
        return RETRIEVER_NAME;
    }

    @Override
    public boolean isCoverLocal() 
    {
        return false;
    }

}
