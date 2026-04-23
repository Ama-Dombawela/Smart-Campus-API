package com.smartcampus.api.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Ama Dombawela 
 * UOW No: W2120682 
 * IIT Student No: 20231642
 *
 * Smart Campus API - Application Configuration Configures JAX-RS for the
 * application. Sets the base path for all API endpoints to /api/v1
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

}
