package kr.co.gaia012.actors;

import com.fasterxml.jackson.databind.JsonNode;
import kr.co.gaia012.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class RestTemplateTest {

    private URI baseUri;
    private ConfigurableApplicationContext server;
    private RestTemplate restTemplate;
    private MovieRepository movieRepository;
    private URI movieUri;

    @Before
    public void setUp() throws Exception {
        this.server = new SpringApplicationBuilder()
                .properties(Collections.singletonMap("server.port", "0"))
                .sources(Application.class).run();

        int port = this.server.getEnvironment().getProperty("local.server.port", Integer.class, 8080);

        this.restTemplate = this.server.getBean(RestTemplate.class);
        this.baseUri = URI.create("http://localhost:" + port + "/");
        this.movieUri = URI.create(this.baseUri.toString() + "movies");
    }

    @After
    public void tearDown() throws Exception {
        if (null != this.server) {
            this.server.close();
        }
    }

    @Test
    public void testRestTemplate() throws Exception {
        ResponseEntity<Movie> postMovieResponseEntity = this.restTemplate
                .postForEntity(movieUri, new Movie("Forest Gump"), Movie.class);
        URI uriOfNewMovie = postMovieResponseEntity.getHeaders().getLocation();
        log.info("the new movie lives at " + uriOfNewMovie);

        JsonNode mapForMovieRecord = this.restTemplate.getForObject(uriOfNewMovie, JsonNode.class);
        log.info("\t..read as a Map.class: " + mapForMovieRecord);

        assertEquals(mapForMovieRecord.get("title").asText(), postMovieResponseEntity.getBody().getTitle());

        Movie movieReference = this.restTemplate.getForObject(uriOfNewMovie, Movie.class);

        assertEquals(movieReference.getTitle(), postMovieResponseEntity.getBody().getTitle());

        log.info("\t..read as a Movie.class: " + movieReference);

        ResponseEntity<Movie> movieResponseEntity = this.restTemplate.getForEntity(uriOfNewMovie, Movie.class);

        assertEquals(movieResponseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(movieResponseEntity.getHeaders().getContentType(), MediaType.parseMediaType("application/json;charset=UTF-8"));
        log.info("\t..read as a ResponseEntity<Movie> : " + movieResponseEntity);


        ParameterizedTypeReference<Resources<Movie>> movies = new ParameterizedTypeReference<Resources<Movie>>() {
        };

        ResponseEntity<Resources<Movie>> moviesResponseEntity = this.restTemplate.exchange(this.movieUri, HttpMethod.GET, null, movies);
        Resources<Movie> movieResources = moviesResponseEntity.getBody();
        movieResources.forEach((a) -> this.log.info("{}", a));

        assertEquals(movieResources.getContent().size(), this.movieRepository.count());
        assertTrue(movieResources.getLinks().stream().filter(m -> m.getRel().equals("self")).count() == 1);
    }
}
