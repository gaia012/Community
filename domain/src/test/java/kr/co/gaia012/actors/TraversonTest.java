package kr.co.gaia012.actors;

import kr.co.gaia012.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.client.Traverson;

import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class TraversonTest {

    private ConfigurableApplicationContext server;
    private Traverson traverson;

    @Before
    public void setUp() throws Exception {
        this.server = new SpringApplicationBuilder()
                .properties(Collections.singletonMap("server.port", "0"))
                .sources(Application.class).run();
        this.traverson = this.server.getBean(Traverson.class);
    }

    @After
    public void tearDown() throws Exception {
        if (null != this.server) {
            this.server.close();
        }
    }

    @Test
    public void testTraversion() throws Exception {
        String nameOfMovie = "Cars";

        Resources<Actor> actorResources = this.traverson
                .follow("actors", "search", "by-movie")
                .withTemplateParameters(Collections.singletonMap("movie", nameOfMovie))
                .toObject(new ParameterizedTypeReference<Resources<Actor>>() {
                });

        actorResources.forEach((a) -> log.info("{}", a));
        assertTrue(actorResources.getContent().size() > 0);
        assertEquals(actorResources.getContent().stream()
                .filter(actor -> actor.fullName.equals("Owen Wilson"))
                .collect(Collectors.toList()).size(), 1);
    }

}
