package com.bongladesch.service

import com.bongladesch.adapter.panache.FileMetaDataRepository
import com.bongladesch.entity.FileMetaData
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import io.vertx.core.Vertx
import io.vertx.core.file.OpenOptions
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.NoResultException
import org.jboss.logging.Logger
import java.util.*

@ApplicationScoped
open class FileService {

    @Inject
    private lateinit var log: Logger
    @Inject
    private lateinit var vertx: Vertx
    @Inject
    private lateinit var fileMetaDataRepository: FileMetaDataRepository
    @Inject
    private lateinit var storageService: StorageService

    open suspend fun uploadFile(fileDTO: FileDTO): FileMetaData {
        val id = UUID.randomUUID().toString()
        log.debug("id: $id, name: ${fileDTO.name}, mimeType: ${fileDTO.mimeType}")
        storageService.uploadFile(id, fileDTO.path, fileDTO.mimeType).awaitSuspending()
        val file = FileMetaData()
        file.id = id
        file.name = fileDTO.name
        file.mimeType = fileDTO.mimeType
        try {
            return fileMetaDataRepository.persistFile(file).awaitSuspending()
        } catch (e: DataAccessException) {
            storageService.deleteFile(id).awaitSuspending()
            throw e
        }
    }

    open suspend fun downloadFile(id: String): AsyncFileDTO {
        log.debug("id: $id")
        val file = getFileById(id)
        val options = OpenOptions().setDeleteOnClose(true).setRead(true).setCreate(false).setWrite(false)
        val path = storageService.downloadFile(file.id).awaitSuspending()
        val asyncFile = Uni.createFrom().completionStage(vertx.fileSystem().open(path.toString(), options).toCompletionStage()).awaitSuspending()
        return AsyncFileDTO(file.name, file.mimeType, asyncFile)
    }

    open suspend fun getFileById(id: String): FileMetaData {
        log.debug("id: $id")
        try {
            return fileMetaDataRepository.findById(id).awaitSuspending()
        } catch (e: NoResultException) {
            throw DataAccessException("Cannot find file with id $id")
        }
    }

    open suspend fun listFilesByMimeType(mimeType: String): List<FileMetaData> {
        return fileMetaDataRepository.listByMimeType(mimeType).awaitSuspending()
    }
}
