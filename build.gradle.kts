plugins {
    id("java")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "io.github.goliath7700"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    testImplementation(platform("org.junit:junit-bom:6.0.0"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // Source: https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j:slf4j-simple:2.0.18")

    // Source: https://mvnrepository.com/artifact/net.minestom/minestom
    implementation("net.minestom:minestom:2026.10.05-26.2")
    // MinestomPVP
    implementation("io.github.togar2:MinestomPvP:2026.05.30-26.1.1")
    // JNoise Library
    implementation("de.articdive:jnoise-pipeline:4.1.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "io.github.goliath7700.Main" // Change this to your main class
        }
    }

    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix on the shadowjar file.
    }
}