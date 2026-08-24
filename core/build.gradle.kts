plugins {
    alias(libs.plugins.android.library) apply true
}

android {
    namespace = "com.devaz.minimallauncher.core"
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    
    // Compose (pour les ViewModels qui peuvent avoir besoin de Compose)
    implementation("androidx.compose.runtime:runtime:1.5.4")
    
    // Modules internes
    implementation(project(":domain"))
    
    // Testing
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
