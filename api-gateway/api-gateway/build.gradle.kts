plugins {
	java
	id("org.springframework.boot") version "4.2.0"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "cl.pagoflex"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
}

extra["springCloudVersion"] = "2026.0.0-SNAPSHOT"

dependencies {
    // 1. El Gateway estándar (Reactivo, es el más estable)
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
    
    // 2. Cliente de Eureka
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    
    // 3. ¡LA QUE FALTABA! Para que el Gateway entienda el "lb://" y busque en Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    
    // 4. Dependencias de test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}