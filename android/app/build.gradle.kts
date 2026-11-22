plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room3)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.abitusd.app"
    compileSdk {
        version = release(37) { minorApiLevel = 1 }
    }

    defaultConfig {
        applicationId = "com.abitusd.app"
        minSdk = 29
        targetSdk = 37
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Emulator alias of the development machine; cleartext allowed only in debug.
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:3000/\"")
        }
        release {
            // PENDIENTE: final Vercel URL once the project is deployed (14-INFRAESTRUCTURA.md).
            buildConfigField("String", "API_BASE_URL", "\"https://abitus-d.vercel.app/\"")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        // android.util.Log and similar return defaults in JVM unit tests.
        unitTests.isReturnDefaultValues = true
    }

    androidResources {
        localeFilters += listOf("es", "en")
    }
}

val copyLegalResources =
    tasks.register<CopyLegalResources>("copyLegalResources") {
        legalDir.set(rootProject.layout.projectDirectory.dir("../legal"))
        outputDir.set(layout.buildDirectory.dir("generated/legal-res"))
    }

androidComponents {
    onVariants { variant ->
        variant.sources.res?.addGeneratedSourceDirectory(copyLegalResources, CopyLegalResources::outputDir)
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

ktlint {
    version.set(libs.versions.ktlint)
    android.set(true)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.room3.runtime)
    implementation(libs.androidx.sqlite.bundled)
    ksp(libs.androidx.room3.compiler)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.okhttp.mockwebserver)
    testImplementation(libs.turbine)
}
