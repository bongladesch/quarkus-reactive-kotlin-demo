package com.bongladesch.api

import com.bongladesch.entity.FileMetaData
import com.bongladesch.service.FileDTO
import com.bongladesch.service.FileService
import io.vertx.core.file.AsyncFile
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.Logger
import org.jboss.resteasy.reactive.*
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder
import org.jboss.resteasy.reactive.multipart.FileUpload

@Path("/api/files")
class FileResource {

    @Inject
    private lateinit var log: Logger
    @Inject
    private lateinit var fileService: FileService

    @POST
    @ResponseStatus(201)
    @Path("/upload")
    suspend fun upload(
        @RestForm("file") file: FileUpload,
        @RestForm("metaData") @PartType(MediaType.APPLICATION_JSON) fileMetaDataJSON: FileMetaDataJSON
    ): FileMetaData {
        log.debug("name: ${fileMetaDataJSON.name}, mimeType: ${fileMetaDataJSON.mimeType}")
        return fileService.uploadFile(
            FileDTO(
                fileMetaDataJSON.name,
                fileMetaDataJSON.mimeType,
                file.uploadedFile()
            )
        )
    }

    @GET
    @Path("/download/{id}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    suspend fun download(@PathParam("id") id: String): RestResponse<AsyncFile> {
        log.debug("id: $id")
        val asyncFileDTO = fileService.downloadFile(id)
        return ResponseBuilder.ok(asyncFileDTO.file)
            .header("Content-Disposition", "inline; filename=${asyncFileDTO.name}")
            .header("Content-Type", asyncFileDTO.mimeType).build()
    }

    @GET
    suspend fun listFiles(@QueryParam("mimeType") mimeType: String): RestResponse<List<FileMetaData>> {
        log.debug("mimeType: $mimeType")
        return RestResponse.ok(fileService.listFilesByMimeType(mimeType))
    }

    @GET
    @Path("/{id}")
    suspend fun getFile(id: String): RestResponse<FileMetaData> {
        log.debug("id: $id")
        return RestResponse.ok(fileService.getFileById(id))
    }
}
