package com.example.demo.config;

import com.example.demo.custom.MetricPublishInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer { // extends WebMvcConfigurerAdapter
	
	@Autowired
    private MetricPublishInterceptor metricPublishInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(metricPublishInterceptor);
	}
}
