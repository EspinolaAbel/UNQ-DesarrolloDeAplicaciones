import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.3"
	id("io.spring.dependency-management") version "1.0.13.RELEASE"
	id("org.sonarqube") version "3.4.0.2513"
	war
	kotlin("jvm") version "1.6.21"
	kotlin("kapt") version "1.5.10"
	kotlin("plugin.spring") version "1.6.21"
	kotlin("plugin.jpa") version "1.6.21"
}

sonarqube {
	properties {
		property("sonar.projectKey", "EspinolaAbel_UNQ-DesarrolloDeAplicaciones")
		property("sonar.organization", "espinolaabel")
		property("sonar.host.url", "https://sonarcloud.io")
	}
}

group = "ar.edu.unq.desapp.grupoN"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.springframework.boot:spring-boot-starter-quartz")

	implementation("com.auth0:java-jwt:4.2.1")

	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("javax.cache:cache-api")
	implementation("org.ehcache:ehcache")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	kapt("org.mapstruct:mapstruct-processor:1.5.3.Final")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

	testImplementation(kotlin("test"))
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	testImplementation("com.tngtech.archunit:archunit-junit5:1.0.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
