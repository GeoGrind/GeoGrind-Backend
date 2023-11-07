package com.geogrind.geogrindbackend.dto.s3

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

data class UploadFileDto(
    @get:Size(min = 0, max = 100) @NotNull val bucketName: String,
    @get:Size(min = 0, max = 100) @NotNull val user_account_id: UUID,
    @NotNull val files: Array<MultipartFile>,
)