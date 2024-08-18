plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "2.3.12"
}

group = "org.km"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.km.counter.ApplicationKt")
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.12")
    implementation("io.ktor:ktor-server-netty:2.3.12")
    implementation("io.ktor:ktor-server-cors:2.3.12")
    implementation("io.ktor:ktor-server-default-headers:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
    implementation("io.ktor:ktor-server-call-logging:2.3.12")
    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

ktor {
    fatJar {
        archiveFileName.set("km-counter.jar")
    }
}