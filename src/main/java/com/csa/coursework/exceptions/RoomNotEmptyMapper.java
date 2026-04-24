package com.csa.coursework.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider // Registers the mapper with Jersey automatically
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ApiError error = new ApiError(
            "409 Conflict",
            "ROOM_NOT_EMPTY",
            exception.getMessage()
        );

        return Response.status(Response.Status.CONFLICT)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}