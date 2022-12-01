plugins {
    application
    java
    idea
}

group = "dev.quinteger"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("it.unimi.dsi:fastutil-core:8.5.9")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

for (day in 1..25) {
    tasks.register<JavaExec>(String.format("day%02d", day)) {
        classpath = sourceSets.main.get().runtimeClasspath
        mainClass.set("dev.quinteger.aoc.AOCLauncher")
        group = "solution"
        args = listOf(day.toString())
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "dev.quinteger.aoc.AOCLauncher"
    }
}