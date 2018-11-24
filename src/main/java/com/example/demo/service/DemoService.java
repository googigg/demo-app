package com.example.demo.service;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DemoService {

    private RestTemplate restTemplate;

    public DemoService(@Qualifier("restTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String google() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://google.com", String.class);
        return response.getBody();
    }
}
