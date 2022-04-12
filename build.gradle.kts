buildscript {
    val kotlinVersion: String by extra("1.6.10")
    val composeVersion: String by extra("1.1.0-rc03")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application").version("7.1.2").apply(false)
    id("com.android.library").version("7.1.2").apply(false)
    id("org.jetbrains.kotlin.android").version("1.6.10").apply(false)
    // ConecoCore / ConecoHls 公開で使う
    id("io.github.gradle-nexus.publish-plugin").version("1.1.0")
}

tasks.register("clean") {
    doFirst {
        delete(rootProject.buildDir)
    }
}

// ライブラリ署名情報がなくてもビルドできるようにする
extra["signing.keyId"] = ""
extra["signing.password"] = ""
extra["signing.key"] = ""
extra["ossrhUsername"] = ""
extra["ossrhPassword"] = ""
extra["sonatypeStagingProfileId"] = ""

// 署名情報を読み出す。開発環境では local.properties に署名情報を置いている。
val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    // 読み出して、extra へ格納する
    val properties = java.util.Properties().apply {
        load(secretPropsFile.inputStream())
    }
    properties.forEach { name, value -> extra[name as String] = value }
} else {
    // システム環境変数から読み出す。CI/CD 用
    extra["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    extra["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    extra["sonatypeStagingProfileId"] = System.getenv("SONATYPE_STAGING_PROFILE_ID")
    extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    extra["signing.password"] = System.getenv("SIGNING_PASSWORD")
    extra["signing.key"] = System.getenv("SIGNING_KEY")
}

// Sonatype OSSRH リポジトリ情報
nexusPublishing.repositories.sonatype {
    stagingProfileId.set(extra["sonatypeStagingProfileId"] as String)
    username.set(extra["ossrhUsername"] as String)
    password.set(extra["ossrhPassword"] as String)
    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
    snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
}
