package kr.co.gaia012.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerNotFoundException extends RuntimeException {
    private Long customerId;
}
