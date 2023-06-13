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
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configurations {
	all {
		exclude(module = "spring-boot-starter-logging")
	}
}
tasks.getByName<Jar>("jar") {
	enabled = false
}

//tasks.withType<Test> {
//	useJUnitPlatform()
//}