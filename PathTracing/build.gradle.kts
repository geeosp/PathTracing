plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.geo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.fxml", "javafx.media", "javafx.swing")
}

application {
    mainClass = "geeosp.pathtracing.Main"
}


dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}




tasks.test {
    useJUnitPlatform()
}

tasks.run.get().apply {
    doFirst {
        println("Starting JavaCPUPathTracer...")
    }
}