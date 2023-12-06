package com.geogrind.geogrindbackend.S3

import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import com.geogrind.geogrindbackend.services.s3.S3ServiceImpl
import org.springframework.stereotype.Service
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.s3.S3Client

@Service
class S3TestServiceImpl(
    private val s3Client: S3Client,
    private val userAccountRepository: UserAccountRepository,
    private val userProfileRepository: UserProfileRepository,
) : S3ServiceImpl(
    s3Client = s3Client,
    userAccountRepository = userAccountRepository,
    userProfileRepository = userProfileRepository,
), S3TestService {

    // test creating the bucket
    override suspend fun createBucket(bucketName: String): SdkHttpResponse {
        TODO("Not yet implemented")
    }

    // clean up after testing
    override suspend fun s3CleanUp() {
        TODO("Not yet implemented")
    }
}