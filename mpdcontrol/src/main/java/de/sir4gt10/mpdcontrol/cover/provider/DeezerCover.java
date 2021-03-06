package de.sir4gt10.mpdcontrol.cover.provider;

import org.json.JSONArray;
import org.json.JSONObject;
import de.sir4gt10.mpdcontrol.mpd.AlbumInfo;

/**
 * Fetch cover from Deezer
 */
public class DeezerCover extends AbstractWebCover {

    @Override
    public String[] getCoverUrl(AlbumInfo albumInfo) throws Exception {

        String deezerResponse;
        JSONObject jsonRootObject;
        JSONArray jsonArray;
        String coverUrl;
        JSONObject jsonObject;

        try {
            deezerResponse = executeGetRequest("http://api.deezer.com/search/album?q="
                    + albumInfo.getAlbum() + " " + albumInfo.getArtist()
                    + "&nb_items=1&output=json");
            jsonRootObject = new JSONObject(deezerResponse);
            jsonArray = jsonRootObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                coverUrl = jsonObject.getString("cover");
                if (coverUrl != null) {
                    coverUrl += "&size=big";
                    return new String[] {
                            coverUrl
                    };
                }
            }

        } catch (Exception e) {
            e(DeezerCover.class.toString(), "Failed to get cover URL from Deeze");
        }

        return new String[0];
    }

    @Override
    public String getName() {
        return "DEEZER";
    }
}
