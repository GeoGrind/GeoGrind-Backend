package com.geogrind.geogrindbackend.services.s3

import com.geogrind.geogrindbackend.dto.s3.S3BulkResponseDto
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.http.SdkHttpResponse

// service for s3
interface S3Service {

    suspend fun getBucketFileList(bucketName: String) : List<String>
    suspend fun downloadFile(bucketName: String, fileKey: String): ByteArray
    suspend fun deleteFile(bucketName: String, fileKey: String): SdkHttpResponse
    suspend fun uploadFiles(bucketName: String, files: Array<MultipartFile>) : List<S3BulkResponseDto>
}