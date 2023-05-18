package com.bongladesch.adapter.aws

import com.bongladesch.service.ObjectStoreException
import com.bongladesch.service.StorageService
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@ApplicationScoped
class S3Storage : StorageService {

    private val bucketName = "my-bucket"

    @Inject
    private lateinit var s3: S3AsyncClient

    override fun uploadFile(objectId: String, path: Path, mimeType: String): Uni<Void> {
        return Uni.createFrom()
            .completionStage {
                s3.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(objectId).contentType(mimeType).build(),
                    AsyncRequestBody.fromFile(path)
                )
            }
            .onItem().ignore().andSwitchTo(Uni.createFrom().voidItem())
            .onFailure().transform { throwable -> ObjectStoreException(throwable.message) }
    }

    override fun downloadFile(objectId: String): Uni<Path> {
        val path = Paths.get(UUID.randomUUID().toString())
        return Uni.createFrom()
            .completionStage {
                s3.getObject(
                    GetObjectRequest.builder().bucket(bucketName).key(objectId).build(),
                    AsyncResponseTransformer.toFile(path)
                )
            }
            .onFailure().transform { throwable -> ObjectStoreException(throwable.message) }
            .onItem().transform { path }
    }

    override fun deleteFile(objectId: String): Uni<Void> {
        return Uni.createFrom()
            .completionStage {
                s3.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(objectId).build())
            }
            .onFailure().transform { throwable -> ObjectStoreException(throwable.message) }
            .onItem().ignore().andSwitchTo(Uni.createFrom().voidItem())
    }
}
