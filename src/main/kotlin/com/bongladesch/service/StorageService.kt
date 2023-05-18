package com.bongladesch.service

import io.smallrye.mutiny.Uni
import java.nio.file.Path

interface StorageService {

    fun uploadFile(objectId: String, path: Path, mimeType: String): Uni<Void>

    fun downloadFile(objectId: String): Uni<Path>

    fun deleteFile(objectId: String): Uni<Void>
}
