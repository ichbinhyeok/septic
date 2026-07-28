package com.example.septic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SeoQueryRobotsInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView
    ) {
        if (modelAndView == null || !isDuplicateVariant(request)) {
            return;
        }
        Object page = modelAndView.getModel().get("page");
        if (page instanceof PageMeta pageMeta) {
            modelAndView.addObject("page", pageMeta.withRobots("noindex,follow"));
        }
    }

    private boolean isDuplicateVariant(HttpServletRequest request) {
        return (request.getQueryString() != null && !request.getQueryString().isBlank())
                || !request.getParameterMap().isEmpty();
    }
}
