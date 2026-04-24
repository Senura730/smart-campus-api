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

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final CampusService service = new CampusService();

    @GET
    public Map<String, Object> getSensors(@QueryParam("type") String type, @Context UriInfo uriInfo) {
        List<Sensor> sensors = service.getAllSensors(type);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("count", sensors.size());
        response.put("items", sensors);
        response.put("_links", Map.of("self", uriInfo.getRequestUri().toString()));
        return response;
    }

    @POST
    public Response registerSensor(Sensor newSensor, @Context UriInfo uriInfo) {
        Sensor created = service.addSensor(newSensor);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Sensor registered successfully.");
        payload.put("sensor", created);
        payload.put("_links", Map.of(
            "self", location.toString(), 
            "readings", location.toString() + "/readings"
        ));
        
        return Response.created(location).entity(payload).build();
    }

    // --- NEW: Added getSensorById method to fix the 500 error ---
    @GET
    @Path("/{sensorId}")
    public Map<String, Object> getSensorById(@PathParam("sensorId") String sensorId, @Context UriInfo uriInfo) {
        Sensor sensor = service.getSensor(sensorId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sensor", sensor);
        
        // HATEOAS Links for the specific sensor
        response.put("_links", Map.of(
            "self", uriInfo.getRequestUri().toString(),
            "readings", uriInfo.getRequestUri().toString() + "/readings"
        ));
        
        return response;
    }

    // Sub-Resource Locator remains intact!
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}