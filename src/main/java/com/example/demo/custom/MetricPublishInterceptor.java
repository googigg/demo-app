package com.example.demo.custom;

import io.micrometer.core.instrument.Metrics;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
public class MetricPublishInterceptor implements HandlerInterceptor {

    private static List<String> serviceList = Arrays.asList("getCashierInfoWhenBuyingVASv2");

    private static final String REQ_PARAM_TIMING = "timing";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        if(true || serviceList.contains(getServiceName(handler).get()))
            httpServletRequest.setAttribute(REQ_PARAM_TIMING, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
        if(httpServletRequest.getAttribute(REQ_PARAM_TIMING) != null) {
            long processingTime = System.currentTimeMillis() - (Long) httpServletRequest.getAttribute(REQ_PARAM_TIMING);


            System.out.println("hello");
            // TODO, revise proper way for metric
            Metrics.gauge("demo", processingTime);
            Metrics.counter("demo", "info", "TEST").increment();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) throws Exception {
    }

    private Optional<String> getServiceName(Object handler) {
        if(handler instanceof HandlerMethod) {
            return Optional.of(((HandlerMethod) handler).getMethod().getName());
        }
        return Optional.empty();
    }
}
