package com.example.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@Validated
public class CircuitController {

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    @GetMapping("/circuit1")
    public ResponseEntity<String> circuit1() {

        restTemplate.getForObject("http://google.com:81", String.class);
        return ResponseEntity.ok("Hello");

        // it jumps to fallback method due to TIMEOUT exception
    }

    // ---------------------------


    @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.timeout.enabled", value = "false"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            })
    @GetMapping("/circuit2")
    public ResponseEntity<String> circuit2() {

        restTemplate.getForObject("http://google.com:81", String.class);
        return ResponseEntity.ok("Hello");

        // wating hystrix timeout, then return timed-out and fallback failed. like below
        // "developer_message": "circuit1 timed-out and fallback failed."
    }

    // --------

    @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            })
    @GetMapping("/circuit3")
    public ResponseEntity<String> circuit3() {
        try {
            restTemplate.getForObject("http://googlexxxx.com:81", String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        } catch (ResourceAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // it always return Hello, due to every exception cough in try-catch.

        return ResponseEntity.ok("Hello");
    }

    // ---------------------------


    @HystrixCommand(fallbackMethod = "fallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            })
    @GetMapping("/circuit4")
    public ResponseEntity<String> circuit4() {
        try {
            restTemplate.getForObject("http://googlexxxx.com:81", String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw e; // throw e for hytrix do fallback
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // fallback activated
        return ResponseEntity.ok("Hello");
    }

    // ---------------------------

    private ResponseEntity<String> fallback() {
        return ResponseEntity.ok("Fallback activated");
    }
}
