val springCloudAwsVersion= project.findProperty("springCloudAwsVersion") as String

plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}


tasks.withType<JavaCompile> {
	options.compilerArgs.addAll(listOf("-parameters"))
}


repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.session:spring-session-core")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
//	implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:$springCloudAwsVersion"))
//	implementation("io.awspring.cloud:spring-cloud-aws-starter-s3:$springCloudAwsVersion")
//	implementation("com.amazonaws:aws-java-sdk-s3:1.12.720")
	implementation("software.amazon.awssdk:s3:2.25.52")
	implementation("org.modelmapper:modelmapper:3.2.0")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
