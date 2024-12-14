// Top-level build file where you can add configuration options common to all sub-projects/modules.

import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.api.AndroidBasePlugin

plugins {
    alias(libs.plugins.agp.app) apply false
}

val defaultAppVerName by extra(tryFetchGitCommitTag())
val defaultAppVerCode by extra(tryFetchGitCommitCount())
val defaultAppPackageName by extra("cn.buffcow.migms")

val androidMinSdkVersion by extra(34)
val androidTargetSdkVersion by extra(35)
val androidCompileSdkVersion by extra(35)
val androidBuildToolsVersion by extra("34.0.0")
val androidSourceCompatibility by extra(JavaVersion.VERSION_21)
val androidTargetCompatibility by extra(JavaVersion.VERSION_21)


subprojects {
    plugins.withType(AndroidBasePlugin::class.java) {
        extensions.configure(CommonExtension::class.java) {
            compileSdk = androidCompileSdkVersion
            buildToolsVersion = androidBuildToolsVersion

            defaultConfig {
                minSdk = androidMinSdkVersion
                if (this is ApplicationDefaultConfig) {
                    versionCode = defaultAppVerCode
                    versionName = defaultAppVerName
                    targetSdk = androidTargetSdkVersion
                    applicationId = namespace
                }
                if (this@subprojects.name == "app") namespace = defaultAppPackageName
            }

            lint {
                abortOnError = true
                checkReleaseBuilds = false
            }

            compileOptions {
                sourceCompatibility = androidSourceCompatibility
                targetCompatibility = androidTargetCompatibility
            }
        }
    }

    plugins.withType(JavaPlugin::class.java) {
        extensions.configure(JavaPluginExtension::class.java) {
            sourceCompatibility = androidSourceCompatibility
            targetCompatibility = androidTargetCompatibility
        }
    }
}

fun tryFetchGitCommitCount(): Int = kotlin.runCatching {
    ProcessBuilder("git", "rev-list", "--count", "HEAD").start().let { process ->
        process.waitFor()
        process.inputStream.bufferedReader().readText().trim().toInt()
    }
}.getOrDefault(1)

fun tryFetchGitCommitTag(): String = kotlin.runCatching {
    ProcessBuilder("git", "describe", "--tags", "--long").start().let { process ->
        process.waitFor()
        val output = process.inputStream.bufferedReader().readText().trim()
        val split = output.split('-')
        if (split.size < 3) throw NullPointerException()
        "${split[0].filter { it.isDigit() || it == '.' }}.${split[1]}"
    }
}.getOrDefault("1.0.0")
