package com.geogrind.geogrindbackend.config.s3

import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.services.s3.S3Client

// configure s3 for files and images storage
@Configuration
interface S3Config {
    fun s3Client() : S3Client
}