package com.csa.coursework.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ApiError error = new ApiError(
            "422 Unprocessable Entity",
            "LINKED_RESOURCE_MISSING",
            exception.getMessage()
        );

        // We use 422 here to satisfy the specific coursework requirement
        return Response.status(422)
                       .entity(error)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}