package com.smartcampus.api.config;


import com.smartcampus.api.exception.mapper.GlobalExceptionMapper;
import com.smartcampus.api.exception.mapper.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.api.exception.mapper.RoomNotEmptyExceptionMapper;
import com.smartcampus.api.exception.mapper.SensorUnavailableExceptionMapper;

import com.smartcampus.api.filter.LoggingFilter;

import com.smartcampus.api.resource.DiscoveryResource;
import com.smartcampus.api.resource.RoomResource;
import com.smartcampus.api.resource.SensorReadingResource;
import com.smartcampus.api.resource.SensorResource;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * @author Ama Dombawela 
 * UOW No: W2120682 
 * IIT Student No: 20231642
 *
 * Smart Campus API - Application Configuration Configures JAX-RS for the
 * application. 
 * Sets the base path for all API endpoints to /api/v1
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    
    /**
     * Registers all JAX-RS components (resources, filters, exception mappers)
     * @return set of all API components to be registered in JAX-RS runtime
     */
    @Override
    public Set<Class<?>> getClasses(){
        Set<Class<?>> resources = new HashSet<>();
        
        //Resources
        resources.add(DiscoveryResource.class);
        resources.add(RoomResource.class);
        resources.add(SensorResource.class);
        resources.add(SensorReadingResource.class);
        
        //Exception Mappers
        resources.add(GlobalExceptionMapper.class);
        resources.add(LinkedResourceNotFoundExceptionMapper.class);
        resources.add(RoomNotEmptyExceptionMapper.class);
        resources.add(SensorUnavailableExceptionMapper.class);
        
        //Filters
        resources.add(LoggingFilter.class);
        
        return resources;
        
    }

}
