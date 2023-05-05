package com.bongladesch.adapter.panache

import com.bongladesch.entity.FileMetaData
import com.bongladesch.service.DataAccessException
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase
import io.quarkus.hibernate.reactive.panache.common.WithSession
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithSession
@ApplicationScoped
class FileMetaDataRepository : PanacheRepositoryBase<FileMetaData, String> {

    @WithTransaction
    fun persistFile(fileMetaData: FileMetaData): Uni<FileMetaData> {
        return persist(fileMetaData).onFailure().transform { t -> DataAccessException(t.message) }
    }

    fun listByMimeType(mimeType: String): Uni<List<FileMetaData>> {
        return list("mimeType", mimeType)
    }
}
