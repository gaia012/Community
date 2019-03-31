package kr.co.gaia012.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/v2", produces = "application/hal+json")
public class CustomerHypermediaRestController {

    private final CustomerResourceAssembler customerResourceAssembler;

    @Autowired
    public CustomerHypermediaRestController(CustomerResourceAssembler customerResourceAssembler) {
        this.customerResourceAssembler = customerResourceAssembler;
    }

    @GetMapping
    ResponseEntity<Resources<Object>> root() {
        Resources<Object> objects = new Resources<>(Collections.emptyList());
        URI uri = MvcUriComponentsBuilder
                .fromMethodCall(MvcUriComponentsBuilder.on(getClass()).getCollection())
                .build().toUri();
        Link lint = new Link(uri.toString(), "customers");
        objects.add(lint);

        return ResponseEntity.ok(objects);
    }

    @GetMapping("customers")
    ResponseEntity<Resources<Resource<Customer>>> getCollection() {
        List<Resource<Customer>> collect =
                new ArrayList<Customer>()
                        .stream().map(customerResourceAssembler::toResource)
                        .collect(Collectors.<Resource<Customer>>toList());

        Resources<Resource<Customer>> resources = new Resources<>(collect);
        URI self = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build().toUri();
        resources.add(new Link(self.toString(), "self"));

        return ResponseEntity.ok(resources);
    }

    @RequestMapping(value = "/customers", method = RequestMethod.OPTIONS)
    ResponseEntity<?> optinos() {
        return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD,
                        HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.DELETE)
                .build();
    }

    @GetMapping(value = "customers/{id}")
    ResponseEntity<Resource<Customer>> get(@PathVariable Long id) {
        return Optional.of(new Customer())
                .map(c -> ResponseEntity.ok(
                        this.customerResourceAssembler.toResource(c)))
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PostMapping(value = "/customers")
    ResponseEntity<Resource<Customer>> post(@RequestBody Customer c) {
        Customer customer = new Customer();
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/customer/{id}").buildAndExpand(customer.getId()).toUri();

        return ResponseEntity.created(uri)
                .body(this.customerResourceAssembler.toResource(customer));
    }

    @DeleteMapping(value = "customers/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        return Optional.of(new Customer())
                .map(c -> {
//                    delete();
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @RequestMapping(value = "customers/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) {
        return Optional.of(new Customer())
                .map(exists -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping("/customers/{id}")
    ResponseEntity<Resource<Customer>> put(@PathVariable Long id, @RequestBody Customer c) {
        Customer customer = new Customer();
        Resource<Customer> customerResource = this.customerResourceAssembler.toResource(customer);
        URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(selfLink).body(customerResource);
    }

}
