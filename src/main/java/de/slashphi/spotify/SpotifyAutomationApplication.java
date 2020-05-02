package de.slashphi.spotify;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

import de.slashphi.spotify.function.UpdatePlaylist;
import de.slashphi.spotify.properties.ConfigProperties;

import java.io.IOException;
import java.util.function.Function;

import javax.annotation.PostConstruct;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

@SpringBootApplication
@EnableRetry
public class SpotifyAutomationApplication {

    @Autowired
    protected ConfigProperties config;

    @Autowired
    private UpdatePlaylist updatePlaylist;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SpotifyAutomationApplication.class, args);
    }

    @PostConstruct
    public void init() throws ParseException, SpotifyWebApiException, IOException {
        updatePlaylist.apply(null);
    }

    @Bean
    public Function<Object, String> updatePodcastContent()
            throws ParseException, SpotifyWebApiException, IOException {
        return updatePlaylist;
    }

    @Bean
    public SpotifyApi getSpotifyAPi() {
        SpotifyApi spotifyApi = new SpotifyApi.Builder().setAccessToken(config.getInitialAccessToken())
                .setRefreshToken(config.getRefreshToken()).setClientId(config.getClientId())
                .setClientSecret(config.getClientSecret()).build();
        return spotifyApi;
    }
}