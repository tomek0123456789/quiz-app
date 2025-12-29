plugins {
	java
	id("org.springframework.boot") version "3.0.5"
	id("io.spring.dependency-management") version "1.1.0"
}


group = "app.Quiz"
version = "0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
}

// TODO check "jar { }" section in https://spring.io/guides/gs/gradle/


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	implementation("org.apache.logging.log4j:log4j-api")
	implementation("org.apache.logging.log4j:log4j-core")
	implementation("org.projectlombok:lombok")
	implementation("org.mockito:mockito-core:3.12.4")
	testImplementation("junit:junit:4.13.1")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("pl.pragmatists:JUnitParams:1.1.1")	//for @Parameters
//	testImplementation ("org.springframework.boot:spring-boot-starter-test")
	testImplementation ("org.springframework.security:spring-security-test")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")


}

configurations {
	all {
		exclude(module = "spring-boot-starter-logging")
	}
}
tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
//	filter{
//		includeTestsMatching("quiz-app.test")
//	}
}