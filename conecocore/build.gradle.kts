plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    // ライブラリ作成に必要
    `maven-publish`
    signing
}

// ライブラリバージョン
val libraryVersion = "1.0.0"
// ライブラリ名
val libraryName = "conecocore"
// ライブラリの説明
val libraryDescription = "It is a library that connects multiple videos into one."

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFile("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

val androidSourcesJar = tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from("android.sourceSets.main.java.srcDirs")
    from("android.sourceSets.main.kotlin.srcDirs")
}

artifacts {
    archives(androidSourcesJar)
}

signing {
    // ルート build.gradle.kts の extra を見に行く
    useInMemoryPgpKeys(
        rootProject.extra["signing.keyId"] as String,
        rootProject.extra["signing.key"] as String,
        rootProject.extra["signing.password"] as String,
    )
    sign(publishing.publications)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "io.github.takusan23"
                artifactId = libraryName
                version = libraryVersion
                if (project.plugins.hasPlugin("com.android.library")) {
                    from(components["release"])
                } else {
                    from(components["java"])
                }
                artifact(androidSourcesJar)
                pom {
                    // ライブラリ情報
                    name.set(artifactId)
                    description.set(libraryDescription)
                    url.set("https://github.com/takusan23/Coneco/")
                    // ライセンス
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://github.com/takusan23/Coneco/blob/master/LICENSE")
                        }
                    }
                    // 開発者
                    developers {
                        developer {
                            id.set("takusan_23")
                            name.set("takusan_23")
                            url.set("https://takusan.negitoro.dev/")
                        }
                    }
                    // git
                    scm {
                        connection.set("scm:git:github.com/takusan23/Coneco")
                        developerConnection.set("scm:git:ssh://github.com/takusan23/Coneco")
                        url.set("https://github.com/takusan23/Coneco")
                    }
                }
            }
        }
    }
}