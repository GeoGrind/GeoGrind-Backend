import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
}

group = "com.geogrind"
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

	// json serializable + android core + logging
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0") // Use the latest version available

	// database connection pool
	implementation("com.zaxxer:HikariCP:5.0.1")

	// bcrypt and security
	implementation("org.mindrot:jbcrypt:0.4")

	// springfox
	implementation("io.springfox:springfox-boot-starter:3.0.0")

	// kotlinx-coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0-Beta")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.1")

	// spring-hateoas
	implementation("org.springframework.boot:spring-boot-starter-hateoas:3.0.7")

	// spring-webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.0.4")

	// flywaydb-migration
	implementation("org.flywaydb:flyway-core:9.16.0")

	// sendgrid
	implementation("com.sendgrid:sendgrid-java:4.9.3")

	// jwt token
	implementation("io.jsonwebtoken:jjwt:0.12.3")

	// okhttp3
	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.11")

	// security
	implementation("org.springframework.boot:spring-boot-starter-security:3.0.4")

	// AWS
	implementation(platform("software.amazon.awssdk:bom:2.20.26"))
	implementation ("software.amazon.awssdk:s3:2.20.68")
	implementation("software.amazon.awssdk:cloudfront:2.20.68")

	// Spring test to use spring mock web dependency
	implementation("org.springframework:spring-test:6.0.6")

	// Spring data redis dependency
	implementation("org.springframework.boot:spring-boot-starter-data-redis:3.0.4")

	// macos native library
	runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.76.Final:osx-aarch_64")

	developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")
	runtimeOnly("com.h2database:h2:2.1.214")
	runtimeOnly("com.mysql:mysql-connector-j:8.0.32")
	runtimeOnly("org.postgresql:postgresql:42.5.4")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation("org.springframework:spring-test:6.0.6")
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








