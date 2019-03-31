package kr.co.gaia012.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/customers")
public class CustomerRestController {

    @RequestMapping(method = RequestMethod.OPTIONS)
    ResponseEntity<?> options() {
        //@formatter:off

        ResponseEntity responseEntity = ResponseEntity.ok()
                .allow(HttpMethod.GET, HttpMethod.POST,
                        HttpMethod.HEAD, HttpMethod.OPTIONS,
                        HttpMethod.PUT, HttpMethod.DELETE)
                .build();
        //@formatter:on

        return responseEntity;
    }

    @GetMapping
    ResponseEntity<Collection<Customer>> getCollection() {
        List list = new ArrayList<>();
        list.add(createCustomer());
        log.info("getCollection()");
        return ResponseEntity.ok(list);
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<Customer> get(@PathVariable Long id) {
        log.info("get");
        return Optional.of(new Customer(2L, "gaia", "gaia@naver.com"))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NullPointerException());
    }

    @PostMapping
    ResponseEntity<Customer> post(@RequestBody Customer c) {
        Customer customer = new Customer(3L, "gaia3", "gaia3@navaer.com");
        log.info("post");
        URI uri = MvcUriComponentsBuilder
                .fromController(getClass()).path("/{id}")
                .buildAndExpand(customer.getId()).toUri();
        return ResponseEntity.created(uri).body(customer);
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        return Optional.of(createCustomer())
                .map(c -> {
//                    delete();
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new NullPointerException("delete Exception"));
    }

    void delete() {
        throw new NullPointerException();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) {
        log.info("head");
        return Optional.of(createCustomer())
//        return Optional.empty()
                .map(exists -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new NullPointerException("Not Customer"));
    }

    @PutMapping(value = "/{id}")
    ResponseEntity<Customer> put(@PathVariable Long id, @RequestBody Customer c) {
        log.info("put");
        return Optional.of(c)
                .map(existing -> {
//                    save()
                    Customer customer = new Customer(1L, "gaia0123", "gaia0123@naver.com");
                    URI selfLink = URI.create(ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .toUriString());
                    return ResponseEntity.created(selfLink).body(customer);
                })
                .orElseThrow(() -> new NullPointerException());
    }

    private Customer createCustomer() {
        return new Customer(1L, "gaia012", "gaia012@naver.com");
    }
}
