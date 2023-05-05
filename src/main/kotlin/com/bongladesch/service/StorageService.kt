package com.bongladesch.service

import java.io.InputStream

interface StorageService {

    fun uploadFile(objectId: String, fileStream: InputStream, mimeType: String)

    fun downloadFile(objectId: String): InputStream

    fun deleteFile(objectId: String)
}
