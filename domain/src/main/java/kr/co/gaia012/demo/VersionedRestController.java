package kr.co.gaia012.demo;

import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api")
public class VersionedRestController {

    public static final String V1_MEDIA_TYPE_VALUE = "application/vnd.buutiful.demo-v1+json";
    public static final String V2_MEDIA_TYPE_VALUE = "application/vnd.bootiful.demo-v2+json";

    private enum ApiVersion {
        v1, v2
    }

    @Getter
    public static class Greeting {

        private String how;
        private String version;

        public Greeting(String how, ApiVersion version) {
            this.how = how;
            this.version = version.toString();
        }
    }

    @GetMapping(value = "/{version}/hi", produces = APPLICATION_JSON_VALUE)
    Greeting greetWithPathVariable(@PathVariable ApiVersion version) {
        return greet(version, "path-variable");
    }

    @GetMapping(value = "/hi", produces = APPLICATION_JSON_VALUE)
    Greeting greetWithHeader(@RequestHeader("X-API-Version") ApiVersion version) {
        return this.greet(version, "header");
    }

    @GetMapping(value = "hi", produces = V1_MEDIA_TYPE_VALUE)
    Greeting greetWithContentNogotiationV1() {
        return this.greet(ApiVersion.v1, "content-negotiatioin");
    }

    @GetMapping(value = "hi", produces = V2_MEDIA_TYPE_VALUE)
    Greeting greetWithContentNogotiationV2() {
        return this.greet(ApiVersion.v1, "content-negotiatioin");
    }

    private Greeting greet(ApiVersion version, String how) {
        return new Greeting(how, version);
    }
}
