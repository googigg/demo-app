package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;


@Configuration
public class RestTemplateConfiguration {

    private static final String TLS_VERSION = "TLSv1";

    public RestTemplateConfiguration() {
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException  {

        // HttpComponentsClientHttpRequestFactory httpComponentFactory = new HttpComponentsClientHttpRequestFactory();
        // httpComponentFactory.setReadTimeout(generalRestTemplateProperties.getReadTimeout());
        // httpComponentFactory.setConnectTimeout(generalRestTemplateProperties.getConnectionTimeout());
        // httpComponentFactory.setHttpClient(this.httpClient());
        // RestTemplate restTemplate = new RestTemplate(httpComponentFactory);

        // setClientRequestInterceptorToInterceptor(restTemplate);
//
//        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
//            @Override
//            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
//                return false;
//            }
//        });

        RestTemplate restTemplate = new RestTemplate();

        return restTemplate;
    }
}