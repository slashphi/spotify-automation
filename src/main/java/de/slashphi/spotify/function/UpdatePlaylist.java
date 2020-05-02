package de.slashphi.spotify.function;

import java.io.IOException;
import java.util.function.Function;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

import de.slashphi.spotify.service.PlaylistUpdateService;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class UpdatePlaylist implements Function<Object, String> {

    @Autowired
    private PlaylistUpdateService updateService;

    @Override
    public String apply(Object t) {
        try {
            this.execute(t);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SpotifyWebApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "finish";
    }

    public void execute(Object t) throws ParseException, SpotifyWebApiException, IOException {
        log.info("Run update playlist");
        updateService.updatePlaylist();
        log.info("Update playlist finished");
    }
    
}