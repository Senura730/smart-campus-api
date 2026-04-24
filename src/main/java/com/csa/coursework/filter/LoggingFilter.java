package com.csa.coursework.filter; // Updated package name

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Captures request details before they reach the resource
 * and response details before they leave the server.
 */
@Provider 
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    // 1. Log the Incoming Request details
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        
        LOGGER.info(">>> [REQUEST] Method: " + method + " | URI: " + uri);
    }

    // 2. Log the Outgoing Response details
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        int status = responseContext.getStatus();
        
        LOGGER.info("<<< [RESPONSE] HTTP Status: " + status);
    }
}