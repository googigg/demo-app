package com.example.demo.controller;

import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@Validated
public class CircuitController {

    private static final int STOP_AT = 5;
    private static int count = 0;

    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "fallback",
            commandKey = "circuit1",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
    })
    @GetMapping("/circuit1")
    public ResponseEntity<String> circuit1() {

        restTemplate.getForObject("http://google.com:81", String.class);
        return ResponseEntity.ok("Hello");

        // it jumps to fallback method due to hystrix TIMEOUT exception
    }

    // ---------------------------


    @HystrixCommand(fallbackMethod = "fallback",
            commandKey = "circuit2",
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
            commandKey = "circuit3",
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
            commandKey = "circuit4",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
            }, ignoreExceptions = ArithmeticException.class)
    @GetMapping("/circuit4")
    public ResponseEntity<String> circuit4() {
        count++;
        try {
            if(count < STOP_AT || (count > 8 && count < 13) ) {
                restTemplate.getForObject("http://googlexxxx.com:81", String.class);
                return ResponseEntity.ok("Hello, " + getHystrixStatus("circuit4"));
            }
            else {
                restTemplate.getForObject("http://google.com", String.class);
                HystrixCircuitBreaker.Factory.getInstance(HystrixCommandKey.Factory.asKey("circuit4")).markSuccess();
                throw new ArithmeticException("this is expected exception");
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw e;
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw e; // throw e for hytrix do fallback
        }

        // need to force closed circuit breaker manually
    }

    // ---------------------------



    @HystrixCommand(fallbackMethod = "fallback",
            commandKey = "circuit5",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2")
            })
    @GetMapping("/circuit5")
    public ResponseEntity<String> circuit5() {
        count++;
        try {
            if(count < STOP_AT) {
                restTemplate.getForObject("http://googlexxxx.com:81", String.class);
                return ResponseEntity.ok("Hello, " + getHystrixStatus("circuit5"));
            }
            else {
                restTemplate.getForObject("http://google.com", String.class);
            }

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw e;
        } catch (ResourceAccessException e) {
            e.printStackTrace();
            throw e; // throw e for hystrix do fallback
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        // fallback activated
        return ResponseEntity.ok("Hello, " + getHystrixStatus("circuit5"));
    }

    // ---------------------------

    private ResponseEntity<String> fallback() {
        throw new ArithmeticException("exception inside fallback " + getHystrixStatus("circuit4") + " " + count);
    }

    private String getHystrixStatus(String commandKey) {
        return (HystrixCircuitBreaker.Factory.getInstance(HystrixCommandKey.Factory.asKey(commandKey)).isOpen()) ? "circuit opened" : "circuit closed";
    }

    @GetMapping("/circuit6")
    public ResponseEntity<String> circuit5(@RequestParam(value = "commandKey", defaultValue = "") String commandKey) {
        try {
            return ResponseEntity.ok("" + getHystrixStatus(commandKey));
        } catch (NullPointerException e) {

            return ResponseEntity.ok(commandKey + " not found");
        }
    }
}
