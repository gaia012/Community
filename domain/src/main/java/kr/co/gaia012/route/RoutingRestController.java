package kr.co.gaia012.route;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.Collections;

@Slf4j
@AllArgsConstructor
@RestController
public class RoutingRestController {

    private static final String FORWARDED_URL = "X-CF-Forwarded-Url";
    private static final String PROXY_METADATA = "X-CF-Proxy-Metadata";
    private static final String PROXY_SIGNALTRUE = "X-CF-Proxy_Signature";
    private final RestOperations restOperations;

    @RequestMapping(headers = {FORWARDED_URL, PROXY_METADATA, PROXY_SIGNALTRUE})
    ResponseEntity<?> service(RequestEntity<byte[]> incoming) {
        log.info("incoming request : " + incoming);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(incoming.getHeaders());
        headers.put("X-CNJ-Name", Collections.singletonList("Cloud Natives"));

        URI uri = headers
                .remove(FORWARDED_URL)
                .stream()
                .findFirst()
                .map(URI::create)
                .orElseThrow(() -> new IllegalStateException(String.format("No %s header present", FORWARDED_URL)));

        RequestEntity<?> outgoing = new RequestEntity<>(
                ((RequestEntity<?>) incoming).getBody(), headers, incoming.getMethod(), uri);

        log.info("outgoing request : {}", outgoing);

        return this.restOperations.exchange(outgoing, byte[].class);
    }
}
