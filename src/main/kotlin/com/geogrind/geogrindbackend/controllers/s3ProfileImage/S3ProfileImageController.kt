package com.geogrind.geogrindbackend.controllers.s3ProfileImage

import com.geogrind.geogrindbackend.dto.s3.S3BulkResponseDto
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.http.SdkHttpResponse

@Tag(name = "S3", description = "S3 REST Controller")
@RestController
@RequestMapping(path = ["/geogrind/profile_image/"])
interface S3ProfileImageController {

    @GetMapping(path = ["download_all_profile_images"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get all files exist in the S3 bucket",
        operationId = "getAllS3Files",
        description = "Get all S3 files from a given bucket"
    )
    suspend fun getFileList(
        request: HttpServletRequest
    ) : ResponseEntity<List<String>>

    @DeleteMapping(path = ["delete_profile_image"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "DELETE",
        summary = "Delete a file from the S3 bucket",
        operationId = "deleteS3File",
        description = "Delete a provided file from a provided S3 Bucket"
    )
    suspend fun deleteFile(
        request: HttpServletRequest
    ) : ResponseEntity<SdkHttpResponse>

    @GetMapping(path = ["download_profile_image"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "GET",
        summary = "Get a file from the S3 bucket",
        operationId = "getS3File",
        description = "Get a provided file from a provided S3 Bucket"
    )
    suspend fun downloadFile(
        request: HttpServletRequest
    ) : ResponseEntity<ByteArray>

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
    suspend fun uploadFile(
        @RequestPart("files") uploadFiles : Array<MultipartFile>,
        request: HttpServletRequest,
    ) : ResponseEntity<List<S3BulkResponseDto>>
}