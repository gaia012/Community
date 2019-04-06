package kr.co.gaia012.config;

import org.springframework.context.annotation.Configuration;
//import org.springframework.boot.web.embedded.
import org.springframework.context.event.EventListener;

import java.net.URI;

@Configuration
public class TraversonConfiguration {

    private int port;
    private URI baseUri;

//    @EventListener
//    public void embeddedPortAvailable(EmbeddedServletContainerInitializedEvent e){
//        this.port = e.
//    }

}
