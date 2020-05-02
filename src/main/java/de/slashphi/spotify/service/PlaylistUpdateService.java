package de.slashphi.spotify.service;

import java.io.IOException;
import java.util.List;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.UnauthorizedException;
import com.wrapper.spotify.model_objects.IPlaylistItem;
import com.wrapper.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import com.wrapper.spotify.model_objects.specification.Episode;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import de.slashphi.spotify.properties.ConfigProperties;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@EnableRetry
public class PlaylistUpdateService {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private PodcastService podcastService;

    @Autowired
    private SpotifyApi spotifyApi;

    @Autowired
    private ConfigProperties config;


    
    
    @Recover
    public void recover(UnauthorizedException exception) {
      log.error("Method failed");
    }

    @Retryable(value = { UnauthorizedException.class }, maxAttempts = 3)
    public void updatePlaylist() throws ParseException, SpotifyWebApiException, IOException {

        log.info("Start checking playlist");
        try {

            String playlist_id = config.getPlaylistId();
            // Remove fully played stuff
            Playlist playlist = playlistService.getPlaylist(playlist_id);
            log.info("Number of tracks: " + playlist.getTracks().getTotal());
            log.info("Number of tracks: " + playlist.getTracks().getItems().length);
            for (PlaylistTrack item : playlist.getTracks().getItems()) {
                IPlaylistItem track = item.getTrack();
                log.info(track.getType().name());
                if (ModelObjectType.EPISODE.equals(track.getType())) {
                    Episode episode = (Episode) item.getTrack();
                    episode = podcastService.getEpisode(episode.getId());
                    if (episode.getResumePoint() != null && episode.getResumePoint().getFullyPlayed()) {
                        log.info("Track is fully played: " + episode.getName());
                        playlistService.removeTrack(playlist_id, episode.getUri());
                    } else {
                        log.info("Track is not fully played: " + episode.getId());
                    }
                } else {
                    Track t = (Track) item.getTrack();
                    log.info("Track is fully played: " + t.getName());
                    playlistService.removeTrack(playlist_id, t.getUri());
                }
            }
            // Add new stuff
            String podcast_id = config.getPodcastId();
            List<Episode> episodes = podcastService.getNewAndUnhearedEpisodes(podcast_id);
            for (Episode episode : episodes) {
                if(!playlistService.trackExists(playlist_id, episode.getUri())){
                    playlistService.addTrack(playlist_id, episode.getUri());
                }
            }
        } catch (UnauthorizedException ex) {
            this.refreshToken(ex);
            throw ex;
        }
    }

    private void refreshToken(UnauthorizedException ex) {
        log.info(ex.getLocalizedMessage());
        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = this.spotifyApi.authorizationCodeRefresh()
                .build();
        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Set access and refresh token for further "spotifyApi" object usage
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            System.out.println("Expires in: " + authorizationCodeCredentials.getExpiresIn());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}