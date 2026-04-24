package com.csa.coursework;

import com.csa.coursework.service.CampusService;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final CampusService service = new CampusService(); // Calls the central service

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Map<String, Object> getHistoryBySensor(@Context UriInfo uriInfo) {
        List<SensorReading> readings = service.getReadings(sensorId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sensorId", sensorId);
        response.put("count", readings.size());
        response.put("items", readings);
        response.put("_links", Map.of("self", uriInfo.getRequestUri().toString()));
        
        return response;
    }

    @POST
    public Response addReading(SensorReading newReading, @Context UriInfo uriInfo) {
        SensorReading created = service.addReading(this.sensorId, newReading);
        
        // HATEOAS requires the specific URI of the newly created resource
        URI location = uriInfo.getRequestUriBuilder().path(created.getId() != null ? created.getId() : "new").build();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Reading added successfully.");
        payload.put("reading", created);
        payload.put("_links", Map.of("self", location.toString()));
        
        return Response.created(location).entity(payload).build();
    }
}