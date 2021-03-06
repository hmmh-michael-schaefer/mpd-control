package de.sir4gt10.mpdcontrol.cover.provider;

import org.json.JSONArray;
import org.json.JSONObject;
import de.sir4gt10.mpdcontrol.mpd.AlbumInfo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fetch cover from MusicBrainz
 */
public class MusicBrainzCover extends AbstractWebCover {

    private static final String COVER_ART_ARCHIVE_URL = "http://coverartarchive.org/release-group/";

    private List<String> extractImageUrls(String covertArchiveResponse) 
    {
        JSONObject jsonRootObject;
        JSONArray jsonArray;
        String coverUrl;
        JSONObject jsonObject;
        List<String> coverUrls = new ArrayList<String>();

        if (covertArchiveResponse == null || covertArchiveResponse.isEmpty()) 
        {
            return Collections.emptyList();
        }

        try 
        {
            jsonRootObject = new JSONObject(covertArchiveResponse);
            if (jsonRootObject.has("images")) 
            {

                jsonArray = jsonRootObject.getJSONArray("images");
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.has("image")) {
                        coverUrl = jsonObject.getString("image");
                        if (coverUrl != null) {
                            coverUrls.add(coverUrl);
                        }
                    }
                }
            }
        } 
        catch (Exception e) 
        {
            d(MusicBrainzCover.class.getName(), "No cover in CovertArchive : " + e);
        }

        return coverUrls;

    }

    private List<String> extractReleaseIds(String response) {

        List<String> releaseList = new ArrayList<String>();

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();

            xpp.setInput(new StringReader(response));
            int eventType;
            eventType = xpp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("release-group")) {
                        String id = xpp.getAttributeValue(null, "id");
                        if (id != null) {
                            releaseList.add(id);
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e(MusicBrainzCover.class.getName(), "Musicbrainz release search failure : " + e);
        }

        return releaseList;

    }

    private String getCoverArtArchiveResponse(String mbid) {

        String request = (COVER_ART_ARCHIVE_URL + mbid + "/");
        return executeGetRequestWithConnection(request);
    }

    @Override
    public String[] getCoverUrl(AlbumInfo albumInfo) throws Exception 
    {
        List<String> releases;
        List<String> coverUrls = new ArrayList<String>();
        String covertArtResponse;

        releases = searchForRelease(albumInfo);
        if (releases == null) return null;
        
        for (String release : releases) 
        {
            covertArtResponse = getCoverArtArchiveResponse(release);
            if (covertArtResponse != null && !covertArtResponse.isEmpty()) 
            {
                coverUrls.addAll(extractImageUrls(covertArtResponse));
            }

            if (!coverUrls.isEmpty())  break;
        }
        
        return coverUrls.toArray(new String[coverUrls.size()]);
    }

    @Override
    public String getName() {
        return "MUSICBRAINZ";
    }

    private List<String> searchForRelease(AlbumInfo albumInfo) {

        String response;

        String url = "http://musicbrainz.org/ws/2/release-group/?query=" + albumInfo.getArtist()
                + " " + albumInfo.getAlbum() + "&type=release-group&limit=5";
        response = executeGetRequestWithConnection(url);
        return extractReleaseIds(response);
    }
}
