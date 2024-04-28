plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("org.jetbrains.intellij") version "1.13.1"
}

group = "mabin"
version = "2.0.8"

repositories {
    maven {
        setUrl("https://maven.aliyun.com/nexus/content/groups/public/")
    }
}

dependencies {
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("java-sdk-core-3.2.4.jar"))))
    implementation("cn.hutool:hutool-http:5.8.26")
    implementation("cn.hutool:hutool-crypto:5.8.26")
    implementation("com.cronutils:cron-utils:9.2.1") {
        exclude(group = "org.slf4j")
    }
}

intellij {
    version.set("2022.2.4")
    type.set("IC")
    plugins.set(listOf("com.intellij.java", "markdown", "properties", "org.jetbrains.plugins.yaml"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("222.3345.118")
        untilBuild.set("233.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
