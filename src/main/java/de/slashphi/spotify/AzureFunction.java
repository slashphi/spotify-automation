package de.slashphi.spotify;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import de.slashphi.spotify.service.PlaylistUpdateService;

import com.microsoft.azure.functions.*;

/**
 * Azure Functions with HTTP Trigger.
 */
public class AzureFunction extends AzureSpringBootRequestHandler<String, String> {

    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it
     * using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/HttpExample 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    @FunctionName("updatePodcastContent")
    public String execute(@TimerTrigger(name = "timerInfo", schedule = "0 */5 * * * *") String timerInfo,
            final ExecutionContext context) throws ParseException, SpotifyWebApiException, IOException {
        context.getLogger().info("Greeting user name: " + timerInfo);
        context.getLogger().info("Java Timer trigger function executed at: " + LocalDateTime.now());
  //      updateService.updatePlaylist();
//        return "Finished";
        return handleRequest(timerInfo, context);
    }
}
