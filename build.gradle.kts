plugins {
	java
	id("org.springframework.boot") version "2.7.18"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories {
	mavenCentral()
	/* kho release của Ignite (để bảo đảm luôn lấy được ext mới) */
	maven { url = uri("https://repo.apache.org/content/repositories/releases/") }
}

/* ---- một biến cho version Ignite, giữ mọi module đồng nhất ---- */
val igniteVer = "2.17.0"

dependencies {
	/* Spring Boot */
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.kafka:spring-kafka")

	/* Apache Ignite core + Spring integration */
	implementation("org.apache.ignite:ignite-core:$igniteVer")
	implementation("org.apache.ignite:ignite-spring:$igniteVer")
	implementation("org.apache.ignite:ignite-client:$igniteVer")               // Thin Client
	implementation("org.apache.ignite:ignite-spring-data-ext:$igniteVer")     // Spring-Data
	implementation("org.apache.ignite:ignite-spring-cache-ext:$igniteVer")    // Spring-Cache

	/* Test */
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	compileOnly("org.projectlombok:lombok:1.18.38")
}

tasks.withType<Test> { useJUnitPlatform() }
