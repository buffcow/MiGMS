@file:Suppress("UnstableApiUsage")

import com.android.SdkConstants
import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import java.util.Properties

plugins {
    alias(libs.plugins.agp.app)
}

val localProp by lazy {
    Properties().apply {
        load(rootProject.file(SdkConstants.FN_LOCAL_PROPERTIES).bufferedReader())
    }
}
var releaseSigningCfg: ApkSigningConfig? = null

android {
    localProp["sign.storeFile"]?.let(::file)?.takeIf { it.exists() }?.let { store ->
        signingConfigs {
            create("release") {
                enableV3Signing = true
                storeFile = store
                keyAlias = localProp.getProperty("sign.keyAlias")
                keyPassword = localProp.getProperty("sign.keyPassword")
                storePassword = localProp.getProperty("sign.storePassword")
            }.also { releaseSigningCfg = it }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        all {
            signingConfig = releaseSigningCfg ?: signingConfigs["debug"]
        }
    }

    applicationVariants.all {
        outputs.all {
            val appName = rootProject.name
            val newApkName = "$appName-${versionName}_$versionCode.apk"
            (this as BaseVariantOutputImpl).outputFileName = newApkName
        }
    }
}

dependencies {
}
