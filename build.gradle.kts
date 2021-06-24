import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  idea
  `java-library`

  id("com.github.johnrengelman.shadow") version "7.0.0"
  kotlin("jvm") version "1.5.10"
  kotlin("plugin.serialization") version "1.5.10"
}

group = "org.menudocs"
version = "1.0-SNAPSHOT"

//sourceCompatibility = "16"
//targetCompatibility = "16"

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") { name = "spigotmc-repo" }
  maven("https://oss.sonatype.org/content/groups/public/") { name = "sonatype" }
  maven("https://jitpack.io") { name = "Jitpack" }
}

dependencies {
  compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
  compileOnly("com.github.nkomarn:Honeycomb:1.1")

  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.10")
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
}

//tasks.processResources {
//    from(sourceSets.main.get().resources.srcDirs.first()) {
//        filter(mapOf("version" to version), ReplaceTokens::class.java)
//    }
//}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  jvmTarget = "16"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
  jvmTarget = "16"
}