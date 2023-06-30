package com.nosbielc.app.infrastructure.delivery;

import org.springframework.http.ResponseEntity;

public interface DemoController {
    ResponseEntity<String> sayHello();
}
