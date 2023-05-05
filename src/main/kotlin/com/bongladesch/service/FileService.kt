package com.bongladesch.service

import com.bongladesch.adapter.panache.FileMetaDataRepository
import com.bongladesch.entity.FileMetaData
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.NoResultException
import java.util.*

@ApplicationScoped
open class FileService {

    @Inject
    private lateinit var fileMetaDataRepository: FileMetaDataRepository

    @Inject
    private lateinit var storageService: StorageService

    open suspend fun uploadFile(fileDTO: FileDTO): FileMetaData {
        val id = UUID.randomUUID().toString()
        storageService.uploadFile(id, fileDTO.fileStream, fileDTO.mimeType)
        val file = FileMetaData()
        file.id = id
        file.name = fileDTO.name
        file.mimeType = fileDTO.mimeType
        try {
            return fileMetaDataRepository.persistFile(file).awaitSuspending()
        } catch (e: DataAccessException) {
            storageService.deleteFile(id)
            throw e
        }
    }

    suspend fun downloadFile(id: String): FileDTO {
        val file = getFileById(id)
        return FileDTO(file.name, file.mimeType, storageService.downloadFile(file.id))
    }

    open suspend fun getFileById(id: String): FileMetaData {
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
