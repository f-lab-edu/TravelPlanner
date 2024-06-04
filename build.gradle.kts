val springCloudAwsVersion= project.findProperty("springCloudAwsVersion") as String

plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
}


tasks.withType<JavaCompile> {
	options.compilerArgs.addAll(listOf("-parameters"))
}


repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.5")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.session:spring-session-core")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
	implementation("software.amazon.awssdk:s3:2.25.52")
	implementation("org.modelmapper:modelmapper:3.2.0")
	compileOnly("org.projectlombok:lombok")
	implementation("com.h2database:h2:2.2.224")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testAnnotationProcessor("org.projectlombok:lombok")
	testCompileOnly("org.projectlombok:lombok")
	testImplementation("org.springframework.security:spring-security-test")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}