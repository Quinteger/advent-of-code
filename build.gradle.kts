plugins {
    application
    java
    idea
    kotlin("jvm") version "2.2.21"
}

group = "dev.quinteger"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("it.unimi.dsi:fastutil-core:8.5.11")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

for (year in 2022..2025) {
    for (day in 1..25) {
        tasks.register<JavaExec>("y%4d-d%02d".format(year, day)) {
            classpath = sourceSets.main.get().runtimeClasspath
            mainClass.set("dev.quinteger.aoc.AOCLauncher")
            group = "solve-y%4d".format(year)
            args = listOf("-y", year.toString(), "-d", day.toString())
        }
        tasks.register<JavaExec>("y%4d-d%02d-quinteger".format(year, day)) {
            classpath = sourceSets.main.get().runtimeClasspath
            mainClass.set("dev.quinteger.aoc.AOCLauncher")
            group = "solve-y%4d-user-quinteger".format(year)
            args = listOf("-y", year.toString(), "-d", day.toString(), "-u", "quinteger")
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "dev.quinteger.aoc.AOCLauncher"
    }
}