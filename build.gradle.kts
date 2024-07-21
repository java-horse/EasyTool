plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "easy.tool"
version = "2.1.6-beat"

repositories {
    maven {
        setUrl("https://maven.aliyun.com/nexus/content/groups/public/")
    }
}

dependencies {
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("java-sdk-core-3.2.4.jar"))))
    implementation("cn.hutool:hutool-http:5.8.28")
    implementation("cn.hutool:hutool-crypto:5.8.28")
    implementation("cn.hutool:hutool-setting:5.8.28")
    implementation("cn.hutool:hutool-extra:5.8.28")
    implementation("cn.hutool:hutool-poi:5.8.28")
    implementation("com.cronutils:cron-utils:9.2.1") {
        exclude(group = "org.slf4j")
    }
    implementation("com.google.zxing:core:3.5.3")
    implementation("org.apache.poi:poi-ooxml:5.2.5")
}

intellij {
    // 2022.2.4
    // 2023.3.4
    version.set("2022.2.4")
    type.set("IC")
    plugins.set(listOf(
            "com.intellij.java",
            "markdown",
            "properties",
            "org.jetbrains.plugins.yaml",
            "Git4Idea"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        // 222.3345.118
        // 233.14475.28
        sinceBuild.set("222.3345.118")
        untilBuild.set("241.*")
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
