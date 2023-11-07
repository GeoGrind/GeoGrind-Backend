package com.geogrind.geogrindbackend.controllers.s3ProfileImage

import com.geogrind.geogrindbackend.dto.s3.*
import com.geogrind.geogrindbackend.services.s3.S3Service
import com.geogrind.geogrindbackend.utils.Middleware.JwtAuthenticationFilterImpl
import io.jsonwebtoken.Claims
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.http.SdkHttpResponse
import java.util.UUID

@Tag(name = "S3", description = "S3 REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/profile_image/"])
class S3ProfileImageControllerImpl(
    private val s3service : S3Service,
    private val jwtTokenMiddleWare: JwtAuthenticationFilterImpl,
) : S3ProfileImageController {
    @GetMapping(path = ["download_all_profile_images"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get all files exist in the S3 bucket",
        operationId = "getAllS3Files",
        description = "Get all S3 files from a given bucket"
    )
    override suspend fun getFileList(
        request: HttpServletRequest
    ) : ResponseEntity<List<String>> = withTimeout(timeoutMillis) {
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val bucketName: String = decoded_token["s3_profile_image_bucket_name"] as String

        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.getBucketFileList(
                    GetBucketFileListDto(
                        bucketName = bucketName,
                    )
                )
            )
            .also {
                log.info("Get all files from the S3 bucket successfully: $it")
            }
    }

    @DeleteMapping(path = ["delete_profile_image"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete a file from the S3 bucket",
        operationId = "deleteS3File",
        description = "Delete a provided file from a provided S3 Bucket"
    )
    override suspend fun deleteFile(
        request: HttpServletRequest,
    ) : ResponseEntity<SdkHttpResponse> = withTimeout(timeoutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!,
        )

        val user_account_id = decoded_token["user_id"] as String
        val bucketName: String = decoded_token["s3_profile_image_bucket_name"] as String

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.deleteFile(
                    DeleteFileDto(
                        bucketName = bucketName,
                        user_account_id = UUID.fromString(user_account_id)
                    )
                )
            )
            .also {
                log.info("Successfully delete the file from the S3 bucket")
            }
    }

    @GetMapping(path = ["download_profile_image"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get a file from the S3 bucket",
        operationId = "getS3File",
        description = "Get a provided file from a provided S3 Bucket"
    )
    override suspend fun downloadFile(
        request: HttpServletRequest
    ) : ResponseEntity<ByteArray> = withTimeout(timeoutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val user_account_id = decoded_token["user_id"] as String
        val bucketName: String = decoded_token["s3_profile_image_bucket_name"] as String

        ResponseEntity
            .status(HttpStatus.OK)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.downloadFile(
                    DownloadFileDto(
                        bucketName = bucketName,
                        user_account_id = UUID.fromString(user_account_id),
                    )
                )
            )
            .also {
                log.info("Successfully download the file in the S3 bucket")
            }
    }

    @ApiImplicitParams(*[
        ApiImplicitParam(value = "Files", required = true, name = "files", allowMultiple = true, dataType = "File", paramType = "form")
    ])
    @PostMapping(path = ["upload_profile_image"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "POST",
        summary = "Upload a file to the S3 bucket",
        operationId = "uploadS3File",
        description = "Upload a file to a provided S3 Bucket"
    )
    override suspend fun uploadFile(
        @RequestPart("files") uploadFiles : Array<MultipartFile>,
        request: HttpServletRequest
    ) : ResponseEntity<List<S3BulkResponseDto>> = withTimeout(timeoutMillis) {
        // get the user account id from cookie
        val token: String? = jwtTokenMiddleWare.extractToken(
            request = request,
            cookieName = "JWT-TOKEN",
        )

        val decoded_token: Claims = jwtTokenMiddleWare.decodeToken(
            token = token!!
        )

        val user_account_id = decoded_token["user_id"] as String
        val bucketName: String = decoded_token["s3_profile_image_bucket_name"] as String

        ResponseEntity
            .status(HttpStatus.CREATED)
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                s3service.uploadFiles(
                   UploadFileDto(
                       bucketName = bucketName,
                       files = uploadFiles,
                       user_account_id = UUID.fromString(user_account_id)
                   )
                )
            )
            .also { log.info("Successfully upload the file to the bucket: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(S3ProfileImageControllerImpl::class.java)
        private const val timeoutMillis = 5000L
    }
}