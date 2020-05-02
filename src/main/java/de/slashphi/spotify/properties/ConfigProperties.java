package de.slashphi.spotify.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * ConfigProperties
 */

@Configuration
@ConfigurationProperties(prefix = "spotify")
@Setter
@Getter
public class ConfigProperties {

    private String clientId; 
    private String clientSecret;
    private String initialAccessToken;
    private String refreshToken;
    private String playlistId = "2y1ix4uoGWlBNFy9SNRAMO";
    private String podcastId = "3tMwolVmLPxyXuZPQu26kQ";

}