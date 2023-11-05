package com.geogrind.geogrindbackend.services.s3

import com.geogrind.geogrindbackend.dto.s3.S3BulkResponseDto
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Service
class S3ServiceImpl(
    private var s3Client: S3Client
) : S3Service {
    override suspend fun getBucketFileList(bucketName: String): List<String> {
        return s3Client
            .listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build()).contents()
            .map { s3Object -> s3Object.key() }
    }

    @Async
    override suspend fun downloadFile(bucketName: String, fileKey: String): ByteArray {
        return s3Client.getObject(
            GetObjectRequest.builder().bucket(bucketName).key(fileKey).build()
        ).readAllBytes()
    }

    override suspend fun deleteFile(bucketName: String, fileKey: String): SdkHttpResponse {
        return s3Client.deleteObject(
            DeleteObjectRequest.builder().bucket(bucketName).key(fileKey).build()
        ).sdkHttpResponse()
    }

    override suspend fun uploadFiles(bucketName: String, files: Array<MultipartFile>): List<S3BulkResponseDto> {
        val responses: ArrayList<S3BulkResponseDto> = ArrayList()
        files.forEach { file ->
            run {
                val originFileName: String? = file.originalFilename
                val uuid: String = UUID.randomUUID().toString()
                responses.add(
                s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(uuid).build(),
                    RequestBody.fromBytes(file.bytes))
                    .sdkHttpResponse()
                    .also { x -> log.info("AWS S3 uploadFile \"$originFileName\" as \"$uuid\" to \"$bucketName\" code ${x.statusCode()}") }
                    .let { response ->
                        S3BulkResponseDto(
                            bucket = bucketName,
                            fileKey = uuid,
                            originFileName = originFileName?:"no name",
                            successful = response.isSuccessful,
                            statusCode = response.statusCode()
                        )
                    }
                )
            }
        }
        return responses
    }

    companion object {
        private val log = LoggerFactory.getLogger(S3ServiceImpl::class.java)
    }
}