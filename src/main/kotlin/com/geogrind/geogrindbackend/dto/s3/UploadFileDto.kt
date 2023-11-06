package com.geogrind.geogrindbackend.dto.s3

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UploadFileDto(
    @get:Size(min = 0, max = 100) @NotNull val bucketName: String,
    @NotNull val files: Array<String>,
)