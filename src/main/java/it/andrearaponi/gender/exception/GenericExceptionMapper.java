package it.andrearaponi.gender.exception;

import it.andrearaponi.gender.dto.ErrorResponse;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 * Generic exception mapper to handle any uncaught exceptions.
 * HEY GUYS, THIS IS A JOKE :D DON'T USE IT IN PRODUCTION!
 * Serves as a safety net to ensure consistent responses.
 */
@Provider
@Priority(Priorities.USER - 100)
@ApplicationScoped
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        // Log the exception without stacktrace in production
        LOG.error("Unhandled exception: " + exception.getMessage());

        // For specific exception types, log details at debug level
        if (LOG.isDebugEnabled()) {
            LOG.debug("Exception details:", exception);
        }

        // Create a generic error response
        ErrorResponse errorResponse = new ErrorResponse(501, "I didn’t feel like handling all the exceptions properly, so feel free to check the logs :D");
        return Response.status(Response.Status.NOT_IMPLEMENTED)
                .entity(errorResponse)
                .build();
    }
}
