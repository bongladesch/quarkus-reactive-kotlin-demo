package com.bongladesch.api

import com.bongladesch.service.DataAccessException
import com.bongladesch.service.DataDuplicationException
import com.bongladesch.service.ObjectStoreException
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.inject.Inject
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.server.ServerExceptionMapper

class ErrorHandler {

    @Inject
    lateinit var log: Logger

    @ServerExceptionMapper
    fun mapException(e: DataAccessException): RestResponse<ErrorJSON> {
        return RestResponse.status(Response.Status.NOT_FOUND, ErrorJSON(e.message))
    }

    @ServerExceptionMapper
    fun mapException(e: DataDuplicationException): RestResponse<ErrorJSON> {
        return RestResponse.status(Response.Status.CONFLICT, ErrorJSON(e.message))
    }

    @ServerExceptionMapper
    fun mapException(e: ObjectStoreException): RestResponse<ErrorJSON> {
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, ErrorJSON(e.message))
    }

    @ServerExceptionMapper
    fun mapException(e: Exception): RestResponse<ErrorJSON> {
        log.error(e.message, e)
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, ErrorJSON(e.message))
    }
}

@RegisterForReflection
data class ErrorJSON(val message: String?)
