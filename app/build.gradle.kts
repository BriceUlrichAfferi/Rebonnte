import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}


// Load keystore.properties from the root directory
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        load(FileInputStream(keystorePropertiesFile))
    } else {
        // Warn if the file is missing (useful for debugging)
        println("Warning: keystore.properties not found at ${keystorePropertiesFile.absolutePath}")
    }
}

android {
    namespace = "com.openclassrooms.rebonnte"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.openclassrooms.rebonnte"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val storeFileValue = keystoreProperties["storeFile"]?.let { file(it) }
            storeFile = storeFileValue
            storePassword = keystoreProperties["storePassword"] as? String
            keyAlias = keystoreProperties["keyAlias"] as? String
            keyPassword = keystoreProperties["keyPassword"] as? String

            // Validate required properties
            if (storeFileValue == null || !storeFileValue.exists()) {
                throw GradleException("Missing or invalid 'storeFile' in keystore.properties. Expected a valid keystore file path.")
            }
            if (storePassword == null) {
                throw GradleException("Missing 'storePassword' in keystore.properties.")
            }
            if (keyAlias == null) {
                throw GradleException("Missing 'keyAlias' in keystore.properties.")
            }
            if (keyPassword == null) {
                throw GradleException("Missing 'keyPassword' in keystore.properties.")
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release") // Ensure this is set
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)


    implementation("androidx.compose.material:material:1.6.0")
    implementation(libs.core.ktx)


    // Espresso
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1") {
        exclude(group = "com.android.support", module = "appcompat-v7")
        exclude(group = "com.android.support", module = "support-v4")
        exclude(group = "com.android.support", module = "support-annotations")
        exclude(group = "com.android.support", module = "support-v13")
        exclude(group = "com.android.support", module = "recyclerview-v7")
    }

    implementation("androidx.navigation:navigation-compose:2.8.6")
    implementation("androidx.compose.material3:material3:1.4.0-alpha05")
    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha05")

    implementation(libs.coil.compose)
    implementation ("androidx.work:work-runtime-ktx:2.10.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-appcheck-debug")
    implementation(libs.firebase.messaging.ktx)
    implementation("com.google.accompanist:accompanist-permissions:0.37.0")

    // FirebaseUI
    implementation("com.firebaseui:firebase-ui-auth:8.0.1")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")

    // Koin
    implementation("io.insert-koin:koin-android:4.0.0")
    implementation("io.insert-koin:koin-androidx-compose:4.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation("com.google.firebase:firebase-firestore:24.0.0")
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("io.insert-koin:koin-test:3.2.0")
    testImplementation("com.google.firebase:firebase-auth:21.0.7")
    testImplementation("io.mockk:mockk:1.12.0")


       testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}