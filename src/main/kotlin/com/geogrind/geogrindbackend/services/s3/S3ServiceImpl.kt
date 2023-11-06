package com.geogrind.geogrindbackend.services.s3

import com.geogrind.geogrindbackend.dto.s3.*
import com.geogrind.geogrindbackend.exceptions.user_account.UserAccountNotFoundException
import com.geogrind.geogrindbackend.exceptions.user_profile.UserProfileNotFoundException
import com.geogrind.geogrindbackend.models.user_account.UserAccount
import com.geogrind.geogrindbackend.models.user_profile.UserProfile
import com.geogrind.geogrindbackend.repositories.user_account.UserAccountRepository
import com.geogrind.geogrindbackend.repositories.user_profile.UserProfileRepository
import io.github.cdimascio.dotenv.Dotenv
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.mock.web.MockMultipartFile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.SdkHttpResponse
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList

@Service
class S3ServiceImpl(
    private var s3Client: S3Client,
    private val userProfileRepository: UserProfileRepository,
    private val userAccountRepository: UserAccountRepository,
) : S3Service {

    // Load environment variables from the .env file
    val dotenv = Dotenv.configure().directory(".").load()

    // CloudFront URL
    val cloudFrontUrl: String = dotenv["AWS_CLOUDFRONT_DISTRIBUTION"]

    override suspend fun getBucketFileList(@Valid requestDto: GetBucketFileListDto): List<String> {
        return s3Client
            .listObjectsV2(ListObjectsV2Request.builder().bucket(requestDto.bucketName).build()).contents()
            .map { s3Object -> "$cloudFrontUrl/${s3Object.key()}" }
    }

    @Async
    override suspend fun downloadFile(@Valid requestDto: DownloadFileDto): ByteArray? {
//        return s3Client.getObject(
//            GetObjectRequest.builder().bucket(bucketName).key(fileKey).build()
//        ).readAllBytes()

        // generate CDN caching for faster access
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            requestDto.user_account_id
        )

        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount.get()
        )

        val fileKey: String? = findUserProfile.get().profileImage

        try {
            val cloudFrontUrl: String = "https://$cloudFrontUrl"
            val url = URI(cloudFrontUrl + "/" + fileKey)
            val connection = url.toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if(responseCode == 200) {
                val inputStream = connection.inputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var length: Int

                // read the file
                while (inputStream.read(buffer).also { length = it } != -1) {
                    byteArrayOutputStream.write(buffer, 0, length)
                }

                return byteArrayOutputStream.toByteArray()
            }
        } catch (error: Exception) {
            log.info("Catch error: $error")
            error.printStackTrace()
        }

        return null
    }

    override suspend fun deleteFile(@Valid requestDto: DeleteFileDto): SdkHttpResponse {
        val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
            requestDto.user_account_id
        )

        val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
            user_account = findUserAccount.get()
        )

        val fileKey: String? = findUserProfile.get().profileImage

        return s3Client.deleteObject(
            DeleteObjectRequest.builder().bucket(requestDto.bucketName).key(fileKey).build()
        ).sdkHttpResponse()
    }

    override suspend fun uploadFiles(@Valid requestDto: UploadFileDto): List<S3BulkResponseDto> {
        val tempDir = File("GeoGrind-Backend/src/main/kotlin/com/geogrind/geogrindbackend/asset")

        // Create an array to hold the MultipartFile objects
        val multipartFiles: List<MultipartFile> = requestDto.files.mapIndexed { index, base64Data ->
            val fileBytes = Base64.getDecoder().decode(base64Data)
            val fileName = UUID.randomUUID().toString() // generate a unique file name
            val tempFile = File(tempDir, fileName)
            tempFile.writeBytes(fileBytes)
            val multipartFile = MockMultipartFile(fileName, fileName, null, fileBytes)
            tempFile.deleteOnExit()
            multipartFile
        }

        val responses: ArrayList<S3BulkResponseDto> = ArrayList()
        multipartFiles.forEach { file ->
            run {
                val originFileName: String? = file.originalFilename
                val uuid: String = UUID.randomUUID().toString()

                // save the profile image fileKey to the database
                val findUserAccount: Optional<UserAccount> = userAccountRepository.findById(
                    requestDto.user_account_id
                )

                if(findUserAccount.isEmpty) {
                    throw UserAccountNotFoundException(
                        field = requestDto.user_account_id.toString()
                    )
                }

                val findUserProfile: Optional<UserProfile> = userProfileRepository.findUserProfileByUserAccount(
                    user_account = findUserAccount.get()
                )

                if(findUserProfile.isEmpty) {
                    throw UserProfileNotFoundException(
                        field = findUserAccount.get().toString()
                    )
                }

                findUserProfile.get().profileImage = uuid
                userProfileRepository.save(findUserProfile.get())

                responses.add(
                s3Client.putObject(
                    PutObjectRequest.builder().bucket(requestDto.bucketName).key(uuid).build(),
                    RequestBody.fromBytes(file.bytes))
                    .sdkHttpResponse()
                    .also { x -> log.info("AWS S3 uploadFile \"$originFileName\" as \"$uuid\" to \"${requestDto.bucketName}\" code ${x.statusCode()}") }
                    .let { response ->
                        S3BulkResponseDto(
                            bucket = requestDto.bucketName,
                            fileKey = uuid,
                            originFileName = originFileName?:"no name",
                            successful = response.isSuccessful,
                            statusCode = response.statusCode()
                        )
                    }
                )
            }
        }
        return responses
    }

    companion object {
        private val log = LoggerFactory.getLogger(S3ServiceImpl::class.java)
    }
}