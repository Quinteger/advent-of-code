plugins {
    application
    java
    idea
    kotlin("jvm") version "2.3.0"
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

val days = linkedMapOf(
    2015 to 25,
    2016 to 25,
    2017 to 25,
    2018 to 25,
    2019 to 25,
    2020 to 25,
    2021 to 25,
    2022 to 25,
    2023 to 25,
    2024 to 25,
    2025 to 12,
)

days.forEach { year, amountOfDays ->
    for (day in 1..amountOfDays) {
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