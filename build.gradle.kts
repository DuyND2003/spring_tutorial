plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain { languageVersion.set(JavaLanguageVersion.of(21)) }
}

repositories {
	mavenCentral()
}


dependencies {
	/* Spring Boot */
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.apache.commons:commons-lang3:3.17.0")

	implementation("org.liquibase:liquibase-core:4.31.1")
	/* Test */
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	compileOnly("org.projectlombok:lombok:1.18.38")
	implementation("org.postgresql:postgresql:42.7.5")
}

tasks.withType<Test> { useJUnitPlatform() }
