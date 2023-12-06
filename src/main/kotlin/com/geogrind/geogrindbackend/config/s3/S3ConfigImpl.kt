package com.geogrind.geogrindbackend.config.s3

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.time.Duration

@Configuration
class S3ConfigImpl : S3Config  {

    // Load environment variables from the .env file
    private val dotenv = Dotenv.configure().directory(".").load()

    private val awsRegion: String = dotenv["AWS_S3_REGION"]
    private val awsAccessKey: String = dotenv["AWS_ACCESS_KEY"]
    private val awsSecretKey: String = dotenv["AWS_SECRET_KEY"]

    @Bean(destroyMethod = "close")
    override fun s3Client(): S3Client {
        return S3Client
            .builder()
            .overrideConfiguration(ClientOverrideConfiguration.builder().apiCallTimeout(Duration.ofSeconds(10)).build())
            .region(Region.regions().find { region: Region? -> region.toString() == awsRegion })
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
            .build()
    }
}