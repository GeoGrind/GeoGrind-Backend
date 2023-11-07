package com.geogrind.geogrindbackend.dto.s3

data class S3BulkResponseDto(
    var bucket: String,
    var fileKey: String,
    var originFileName: String,
    var successful: Boolean,
    var statusCode: Int,
)