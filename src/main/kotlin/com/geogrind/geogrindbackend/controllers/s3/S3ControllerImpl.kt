package com.geogrind.geogrindbackend.controllers.s3

import com.geogrind.geogrindbackend.dto.s3.*
import com.geogrind.geogrindbackend.services.s3.S3Service
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.http.SdkHttpResponse
import java.util.Base64

@Tag(name = "S3", description = "S3 REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/s3/"])
class S3ControllerImpl(
    private val s3service : S3Service
) : S3Controller {
    @GetMapping(path = ["download_all_files/{bucket}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get all files exist in the S3 bucket",
        operationId = "getAllS3Files",
        description = "Get all S3 files from a given bucket"
    )
    override suspend fun getFileList(
        @PathVariable(required = true) bucket: String
    ) : ResponseEntity<List<String>> = withTimeout(timeoutMillis) {
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.getBucketFileList(
                    GetBucketFileListDto(
                        bucketName = bucket
                    )
                )
            )
            .also {
                log.info("Get all files from the S3 bucket successfully: $it")
            }
    }

    @DeleteMapping(path = ["delete_file/{bucket}/{file}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete a file from the S3 bucket",
        operationId = "deleteS3File",
        description = "Delete a provided file from a provided S3 Bucket"
    )
    override suspend fun deleteFile(
        @PathVariable(required = true) bucket: String,
        @PathVariable(required = true) fileKey: String
    ) : ResponseEntity<SdkHttpResponse> = withTimeout(timeoutMillis) {
        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.deleteFile(
                    DeleteFileDto(
                        bucketName = bucket,
                        fileKey = fileKey,
                    )
                )
            )
            .also {
                log.info("Successfully delete the file from the S3 bucket")
            }
    }

    @GetMapping(path = ["download_file/{bucket}/{file}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get a file from the S3 bucket",
        operationId = "getS3File",
        description = "Get a provided file from a provided S3 Bucket"
    )
    override suspend fun downloadFile(
        @PathVariable("bucket", required = true) bucket: String,
        @PathVariable("file", required = true) fileKey: String,
    ) : ResponseEntity<ByteArray> = withTimeout(timeoutMillis) {
        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.downloadFile(
                    DownloadFileDto(
                        bucketName = bucket,
                        fileKey = fileKey,
                    )
                )
            )
            .also {
                log.info("Successfully download the file in the S3 bucket")
            }
    }

    @ApiImplicitParams(*[
        ApiImplicitParam(value = "AWS Bucket name", name = "bucket", dataType = "String", paramType = "query"),
        ApiImplicitParam(value = "Files", required = true, name = "files", allowMultiple = true, dataType = "String", paramType = "form")
    ])
    @PostMapping(path = ["upload_file/{bucket}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Upload a file to the S3 bucket",
        operationId = "uploadS3File",
        description = "Upload a file to a provided S3 Bucket"
    )
    override suspend fun uploadFile(
        @PathVariable(required = true) bucket: String,
        @RequestPart("files") uploadFiles : Array<String>
    ) : ResponseEntity<List<S3BulkResponseDto>> = withTimeout(timeoutMillis) {
        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.uploadFiles(
                   UploadFileDto(
                       bucketName = bucket,
                       files = uploadFiles,
                   )
                )
            )
            .also { log.info("Successfully upload the file to the bucket: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(S3ControllerImpl::class.java)
        private const val timeoutMillis = 5000L
    }
}