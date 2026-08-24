plugins {
    alias(libs.plugins.android.library) apply true
}

android {
    namespace = "com.devaz.minimallauncher.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Kotlin
    implementation(libs.kotlin.stdlib)
    
    // AndroidX
    implementation(libs.androidx.core.ktx)
    
    // Room Database (désactivé temporairement - à réactiver plus tard)
    // implementation(libs.androidx.room.runtime)
    // implementation(libs.androidx.room.ktx)
    // kapt(libs.androidx.room.compiler)
    
    // Modules internes
    implementation(project(":core"))
    implementation(project(":domain"))
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
