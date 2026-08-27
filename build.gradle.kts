plugins {
    id("java")
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
    implementation("net.minestom:minestom:2026.08.16-26.2")
}

tasks.test {
    useJUnitPlatform()
}