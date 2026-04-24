package com.csa.coursework.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ApiError error = new ApiError(
            "403 Forbidden",
            "SENSOR_MAINTENANCE_LOCK",
            exception.getMessage()
        );

        return Response.status(Response.Status.FORBIDDEN)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}