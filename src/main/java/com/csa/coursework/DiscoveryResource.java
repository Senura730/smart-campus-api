package com.csa.coursework;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.LinkedHashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Map<String, Object> discover(@Context UriInfo uriInfo) {
        // Dynamically grabs the server address (e.g., http://localhost:8080/api/v1)
        String baseUri = uriInfo.getBaseUri().toString().replaceAll("/$", "");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("api_name", "Smart Campus Sensor API");
        response.put("version", "v1.0");
        
        // Retaining your required administrative contact
        response.put("administrativeContact", Map.of(
            "name", "Senura Damhiru Manamperi",
            "email", "senura.20240924@iit.ac.lk",
            "student_id", "[20240624/w2121228]" 
        ));
        
        // HATEOAS Links to navigate the API
        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms", baseUri + "/rooms");
        links.put("sensors", baseUri + "/sensors");
        
        response.put("_links", links);
        return response;
    }
}