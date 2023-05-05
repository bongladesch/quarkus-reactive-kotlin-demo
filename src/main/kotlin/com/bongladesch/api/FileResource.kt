package com.bongladesch.api

import com.bongladesch.entity.FileMetaData
import com.bongladesch.service.FileDTO
import com.bongladesch.service.FileService
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.PartType
import org.jboss.resteasy.reactive.ResponseStatus
import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.RestResponse
import org.jboss.resteasy.reactive.multipart.FileUpload
import kotlin.io.path.inputStream

@Path("/api/files")
class FileResource {

    @Inject
    private lateinit var fileService: FileService

    @POST
    @ResponseStatus(201)
    @Path("/upload")
    suspend fun upload(
        @RestForm("file") file: FileUpload,
        @RestForm("metaData") @PartType(MediaType.APPLICATION_JSON) fileMetaDataJSON: FileMetaDataJSON
    ): FileMetaData {
        return fileService.uploadFile(
            FileDTO(
                fileMetaDataJSON.name,
                fileMetaDataJSON.mimeType,
                file.uploadedFile().inputStream()
            )
        )
    }

    @GET
    @Path("/download/{id}")
    suspend fun download(@PathParam("id") id: String): Response {
        val fileDTO = fileService.downloadFile(id)
        return Response.ok(fileDTO.fileStream)
            .header("Content-Disposition", "inline; filename=${fileDTO.name}")
            .header("Content-Type", fileDTO.mimeType).build()
    }

    @GET
    suspend fun listFiles(@QueryParam("mimeType") mimeType: String): RestResponse<List<FileMetaData>> {
        return RestResponse.ok(fileService.listFilesByMimeType(mimeType))
    }

    @GET
    @Path("/{id}")
    suspend fun getFile(id: String): RestResponse<FileMetaData> {
        return RestResponse.ok(fileService.getFileById(id))
    }
}
