package com.csa.coursework;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Matching the explicit application loading style.
 * The path is now controlled by web.xml, but we keep the class for Rubric Task 1.1.
 */
@ApplicationPath("/") 
public class RestApplication extends Application {
    // Empty class as required by the JAX-RS specification
}