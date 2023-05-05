package com.bongladesch.adapter.minio

import com.bongladesch.service.StorageService
import io.minio.*
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import java.io.InputStream

@ApplicationScoped
class MinioStorage : StorageService {

    private val bucketName = "my-bucket"

    @Inject
    private lateinit var minioClient: MinioClient

    private fun createBucketIfNotExists() {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()))
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
    }

    override fun uploadFile(objectId: String, fileStream: InputStream, mimeType: String) {
        createBucketIfNotExists()
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectId)
                .contentType(mimeType)
                .stream(fileStream, -1, 50 * 1024 * 1024L)
                .build()
        )
    }

    override fun downloadFile(objectId: String): InputStream {
        return minioClient.getObject(
            GetObjectArgs.builder().bucket(bucketName).`object`(objectId).build()
        )
    }

    override fun deleteFile(objectId: String) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).`object`(objectId).build())
    }
}
