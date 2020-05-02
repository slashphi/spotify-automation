package de.slashphi.spotify.service;

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Episode;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PlaylistService {
    @Autowired
    private SpotifyApi api;

    public Playlist getPlaylist(String playlist_id) throws ParseException, SpotifyWebApiException, IOException {
        return api.getPlaylist(playlist_id).build().execute();
    }

    public void addTrack(String playlist_id, String uri) throws ParseException, SpotifyWebApiException, IOException {
        String[] uris = new String[1];
        uris[0] = uri;
        api.addItemsToPlaylist(playlist_id, uris).build().execute();
    }

    public void removeTrack(String playlistId, String uri) throws ParseException, SpotifyWebApiException, IOException {
        JsonArray tracks = JsonParser.parseString("[{\"uri\":\"" + uri + "\"}]").getAsJsonArray();
        api.removeItemsFromPlaylist(playlistId, tracks).build().execute();
        log.info("Track has been removed: " + uri);
    }

    public boolean trackExists(String playlistId, String uri)
            throws ParseException, SpotifyWebApiException, IOException {
        Paging<PlaylistTrack> tracks = api.getPlaylistsItems(playlistId).build().execute();
        for (PlaylistTrack t : tracks.getItems()) {
            if (ModelObjectType.EPISODE.equals(t.getTrack().getType())) {
                Episode e = (Episode) t.getTrack();
                if (e.getUri().equals(uri)) {
                    return true;
                }
            } else {
                Track e = (Track) t.getTrack();
                if (e.getUri().equals(uri)) {
                    return true;
                }
            }
        }
        return false;
    }

}