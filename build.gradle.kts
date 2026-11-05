plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

javafx {
    version = "25"
    modules = listOf("javafx.controls")
}

application {
    mainClass = "lab2.Main"
}

dependencies {
    // Jackson: JSON-сериализация и десериализация
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.4")

    // Поддержка LocalTime, LocalDateTime и других типов java.time
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.4")

    // JUnit 5
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Запуск JUnit-тестов через Gradle
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}