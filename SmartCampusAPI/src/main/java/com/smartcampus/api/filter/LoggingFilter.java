/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.api.filter;

import java.io.IOException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.util.logging.Logger;
import javax.ws.rs.ext.Provider;

/**
 *Logging Filter
 *Logs all incoming requests and outgoing responses
 * Automatically applied to every API request via @Provider
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    //Logger instance for this class
    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Logs the HTTP method and URI of every incoming reques
     * @param requestContext - the incoming request context
     * @throws IOException if an error occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOGGER.info("====== Incoming Request ======");
        LOGGER.info(() -> "Method : " + requestContext.getMethod());
        LOGGER.info(() -> "Request URI: " + requestContext.getUriInfo().getRequestUri());

    }

    /**
     * Logs the status code of every outgoing response
     * @param requestContext - the incoming request context
     * @param responseContext - the outgoing response context
     * @throws IOException if an error occurs
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        LOGGER.info("====== Outgoing Response ======");
        LOGGER.info(() -> "Response status: " + responseContext.getStatus());

    }
}