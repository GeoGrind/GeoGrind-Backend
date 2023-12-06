package com.geogrind.geogrindbackend.S3

import com.geogrind.geogrindbackend.services.s3.S3Service
import software.amazon.awssdk.http.SdkHttpResponse

interface S3TestService : S3Service {
    suspend fun createBucket(bucketName: String) : SdkHttpResponse
    suspend fun s3CleanUp()
}