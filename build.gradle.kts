val springCloudAwsVersion = project.findProperty("springCloudAwsVersion") as String

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
}

kotlin {
    jvmToolchain(21)
}

jacoco {
    toolVersion = "0.8.12" // specify the JaCoCo version
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-parameters"))
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate6:2.17.1")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.12.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.3.0")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.session:spring-session-core")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    implementation("software.amazon.awssdk:s3:2.25.52")
    implementation("org.modelmapper:modelmapper:3.2.0")
    implementation("org.postgresql:postgresql:42.7.3")
    compileOnly("org.projectlombok:lombok")
    implementation("com.h2database:h2:2.2.224")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-mail:3.3.1")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
    useJUnitPlatform()
    val activeProfiles = "test"
    systemProperty("spring.profiles.active", activeProfiles)
    finalizedBy("jacocoTestReport")
}

// Configure the existing jacocoTestReport task
tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val coverageSourceDirs = files("src/main/java")
    val classFiles = fileTree("build/classes/java/main") {
        exclude(
            "**/Q*",
            "**/domain/model/**",
            "**/presentation/dto/**"
        )
    }
    val executionDataFiles = fileTree("build") {
        include("jacoco/test.exec")
    }

    sourceDirectories.setFrom(coverageSourceDirs)
    classDirectories.setFrom(classFiles)
    executionData.setFrom(executionDataFiles)
}

// Configure the existing jacocoTestCoverageVerification task
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("jacocoTestReport")

    violationRules {
        rule {
            element = "CLASS"
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = BigDecimal("0.75") // 75% minimum coverage
            }
        }
    }

    classDirectories.setFrom(
        fileTree("build/classes/java/main") {
            exclude(
                "**/Q*",
                "**/*Controller.class",
                "**/*.dto.*",
                "**/*.config.*",
                "**/domain/Q*",
                "**/domain/model/**",
                "**/presentation/dto/**"
            )
        }
    )
}

// Ensure the check task does not depend on jacocoTestCoverageVerification
tasks.check {
    dependsOn("jacocoTestReport")
}

// Run jacocoTestCoverageVerification but do not fail the build if it fails
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    doLast {
        val result = state.failure
        if (result != null) {
            println("Code coverage verification failed: ${result.message}")
        } else {
            println("Code coverage verification passed")
        }
    }
}