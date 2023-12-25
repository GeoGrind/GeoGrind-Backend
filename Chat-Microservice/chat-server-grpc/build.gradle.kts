import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")

    // SpringBoot
    id("org.springframework.boot") version "3.1.4"

    // kotlin + spring jpa
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    kotlin("plugin.jpa") version "1.8.22"
    kotlin("plugin.serialization") version "1.5.31"

    // gRPC + WIRE
    id("application")
    id("com.squareup.wire") version "4.9.3"
    application
}

application {
    mainClass.set("io.grpc.kotlin.generator.ChatGrpcServerApplication")
}

kotlin.sourceSets.all {
    languageSettings.optIn("kotlin.RequiresOptIn")
}

group = "io.grpc.kotlin.generator"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    // spring-boot framework
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.4")
    implementation("org.springframework.boot:spring-boot-starter-mustache:3.0.7")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")
    implementation("org.springframework.boot:spring-boot-devtools:3.0.4")

    // JAX-RS
    implementation("org.glassfish.jersey.containers:jersey-container-servlet:3.0.2")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation("org.glassfish.jersey.inject:jersey-hk2:3.0.2")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:3.0.2")

    // database connection pool
    implementation("com.zaxxer:HikariCP:5.0.1")

    // psql driver
    implementation("org.postgresql:postgresql:42.7.1")

    // dotenv
    implementation("io.github.cdimascio:dotenv-java:3.0.0")

    // json serializable + android core + logging
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
    implementation("org.slf4j:slf4j-api:1.7.32")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Wire protocol buffer plugin
wire {
    kotlin {
        includes = listOf("io.grpc.kotlin.generator.*")
        excludes = emptyList()
        exclusive = true
        out = "${layout.buildDirectory}/generated"
        android = false
        javaInterop = false
        buildersOnly = false
        emitDeclaredOptions = false
        emitAppliedOptions = true
        rpcCallStyle = "suspending"
        rpcRole = "server"
        nameSuffix = "Suffix"
        singleMethodServices = false
    }
}

sourceSets {
    main {
        wire {
            sourcePath("src/main/proto")
        }
    }
}