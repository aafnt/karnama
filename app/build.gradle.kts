plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

android {
    namespace = "com.karnama.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.karnama.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // امضای ثابت نسخه release
    //
    // برای اینکه هر آپدیت بعدی روی نصب قبلی برنامه (بدون Uninstall) نصب
    // شود، اندروید الزام می‌کند همه‌ی نسخه‌ها با یک کلید یکسان امضا شوند.
    // به همین دلیل یک Keystore دائمی در مسیر keystore/karnama-release.jks
    // ساخته و به این پروژه اضافه شده؛ همه‌ی Buildهای CI (و لوکال) از همین
    // کلید استفاده می‌کنند مگر اینکه مقادیر را از طریق متغیرهای محیطی
    // بازنویسی کنید (مثلاً برای امنیت بیشتر در یک ریپوی عمومی).
    //
    // نکته‌ی مهم: این فایل Keystore را هرگز پاک یا جایگزین نکنید؛ در غیر
    // این صورت کاربران باید نسخه‌ی قبلی را Uninstall کنند تا آپدیت جدید
    // نصب شود.
    // ─────────────────────────────────────────────────────────────────
    signingConfigs {
        create("release") {
            val ksFile = System.getenv("KARNAMA_KEYSTORE_PATH")
                ?.let { file(it) }
                ?: rootProject.file("keystore/karnama-release.jks")
            storeFile = ksFile
            storePassword = System.getenv("KARNAMA_KEYSTORE_PASSWORD") ?: "karnama-2026-secure"
            keyAlias = System.getenv("KARNAMA_KEY_ALIAS") ?: "karnama"
            keyPassword = System.getenv("KARNAMA_KEY_PASSWORD") ?: "karnama-2026-secure"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // نسخه دیباگ هم با همان کلید release امضا می‌شود تا نصب دیباگ و
            // ریلیز با هم تداخل نکنند و بتوانید هر دو را کنار هم تست کنید
            // بدون نگرانی از عدم تطابق امضا هنگام به‌روزرسانی.
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
