package de.slashphi.spotify.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Episode;
import com.wrapper.spotify.model_objects.specification.EpisodeSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.model_objects.specification.Show;
import com.wrapper.spotify.requests.data.playlists.GetPlaylistRequest.Builder;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class PodcastService {
    @Autowired
    private SpotifyApi api;


    public Show getPodcast(String podcast_id) throws ParseException, SpotifyWebApiException, IOException {
        return api.getShow(podcast_id).build().execute();
    }

    public List<Episode> getNewAndUnhearedEpisodes(
            String podcast_id)
            throws ParseException, SpotifyWebApiException, IOException {
        Paging<EpisodeSimplified> out = api.getShowEpisodes(podcast_id).build().execute();
        
        log.info("Number of total episodes: " + out.getItems().length + "/" + out.getTotal());
        List<Episode> ret = new ArrayList<>();

        EpisodeSimplified[] episodes = out.getItems();
        for (EpisodeSimplified episodeSimplified : episodes) {
            Episode e = this.getEpisode(episodeSimplified.getId());
            if(e.getResumePoint() != null && !e.getResumePoint().getFullyPlayed()){
                log.info("Episode is not fully played: " + e.getName());
                ret.add(e);
            }
        }

        return ret;

    }

    public Episode getEpisode(String id) throws ParseException, SpotifyWebApiException, IOException {
        return api.getEpisode(id).build().execute();
    }

    
}