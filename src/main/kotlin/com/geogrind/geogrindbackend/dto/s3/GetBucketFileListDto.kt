package com.geogrind.geogrindbackend.dto.s3

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class GetBucketFileListDto(
    @get:Size(min = 0, max = 100) @NotNull val bucketName: String,
)