import org.gradle.api.JavaVersion.VERSION_1_8
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    java
    application
    jacoco
    id("org.springframework.boot") version "2.1.0.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

repositories {
    jcenter()
    mavenCentral()
}

group = "de.ostermann"
version = "0.3.0"

application {
    mainClassName = "de.ostermann.pricecrawler.PriceCrawler"
}

java {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

jacoco {
    toolVersion = "0.8.2"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-devtools")

    implementation("com.google.guava:guava:27.0-jre")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("org.apache.commons:commons-lang3:3.8.1")
    implementation("org.apache.commons:commons-text:1.6")
    implementation("com.github.ben-manes.caffeine:caffeine:2.6.2")
    implementation("commons-io:commons-io:2.6")
    implementation("org.jsoup:jsoup:1.11.3")
    implementation("org.json:json:20180813")

    // todo: switch back to lombok plugin as soon as intellij picks up the dependency again
    implementation("org.projectlombok:lombok:1.18.4")
    annotationProcessor("org.projectlombok:lombok:1.18.4")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    test { useJUnitPlatform() }

    fun JavaExec.fixupStdIo() {
        standardInput = System.`in`
        if (System.console() != null || System.getenv()["TERM"]?.startsWith("xterm") == true) {
            systemProperty("spring.output.ansi.enabled", "always")
        }
    }

    named<JavaExec>("run") { fixupStdIo() }
    bootRun { fixupStdIo() }

    withType(JavaCompile::class) {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}
