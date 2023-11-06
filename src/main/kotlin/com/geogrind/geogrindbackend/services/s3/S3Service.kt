package com.geogrind.geogrindbackend.services.s3

import com.geogrind.geogrindbackend.dto.s3.*
import jakarta.validation.Valid
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.http.SdkHttpResponse

// service for s3
interface S3Service {

    suspend fun getBucketFileList(@Valid requestDto: GetBucketFileListDto) : List<String>
    suspend fun downloadFile(@Valid requestDto: DownloadFileDto): ByteArray?
    suspend fun deleteFile(@Valid requestDto: DeleteFileDto): SdkHttpResponse
    suspend fun uploadFiles(@Valid requestDto: UploadFileDto) : List<S3BulkResponseDto>
}