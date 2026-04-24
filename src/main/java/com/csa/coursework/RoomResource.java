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

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final CampusService service = new CampusService();

    @GET
    public Map<String, Object> getAllRooms(@Context UriInfo uriInfo) {
        List<SensorRoom> rooms = service.getAllRooms();
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("count", rooms.size());
        response.put("items", rooms);
        response.put("_links", Map.of("self", uriInfo.getRequestUri().toString()));
        return response;
    }

    @GET
    @Path("/{roomId}")
    public Map<String, Object> getRoomById(@PathParam("roomId") String roomId, @Context UriInfo uriInfo) {
        SensorRoom room = service.getRoom(roomId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("room", room);
        response.put("_links", Map.of("self", uriInfo.getRequestUri().toString()));
        return response;
    }

    @POST
    public Response createRoom(SensorRoom room, @Context UriInfo uriInfo) {
        SensorRoom created = service.addRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", "Room created successfully.");
        payload.put("room", created);
        payload.put("_links", Map.of("self", location.toString()));
        
        return Response.created(location).entity(payload).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        service.deleteRoom(roomId);
        
        Map<String, String> payload = Map.of(
            "message", "Room deleted successfully.", 
            "roomId", roomId
        );
        return Response.ok(payload).build();
    }
}