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
	google()
}

dependencies {
	// spring-boot framework
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-mustache")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("io.github.cdimascio:dotenv-java:3.0.0")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-devtools")

	// bcrypt
	implementation("org.mindrot:jbcrypt:0.4")

	// springfox
	implementation("io.springfox:springfox-boot-starter:3.0.0")

	// kotlinx-coroutines
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.1")

	// spring-hateoas
	implementation("org.springframework.boot:spring-boot-starter-hateoas")

	// spring-webflux
	implementation("org.springframework.boot:spring-boot-starter-webflux")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
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







