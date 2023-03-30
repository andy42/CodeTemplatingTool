import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//    kotlin("multiplatform")
//    id("org.jetbrains.compose")

    val kotlinVersion = "1.8.0"
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    //kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
}

group = "com.jaehl"
version = "1.0-SNAPSHOT"

val coroutinesVersion = "1.3.6"

val daggerVersion by extra("2.39.1")

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)

                // Dagger : A fast dependency injector for Android and Java.
                implementation("com.google.dagger:dagger-compiler:$daggerVersion")

                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")

                implementation ("org.jsoup:jsoup:1.8.3")
                implementation ("com.google.code.gson:gson:2.8.9")
                implementation("com.arkivanov.decompose:decompose:1.0.0")
                implementation("com.arkivanov.decompose:extensions-compose-jetbrains:1.0.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.jaehl.codeTool.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CodeTool"
            packageVersion = "1.0.0"
        }
    }
}
