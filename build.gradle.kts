plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("org.jetbrains.intellij") version "1.10.0"
}

group = "mabin"
version = "2.0.3"

repositories {
    maven {
        setUrl("https://maven.aliyun.com/nexus/content/groups/public/")
    }
}

dependencies {
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("java-sdk-core-3.2.4.jar"))))
    implementation("cn.hutool:hutool-http:5.8.16")
    implementation("javassist:javassist:3.12.1.GA")
}

intellij {
    version.set("2022.2.4")
    type.set("IC")
    plugins.set(listOf("com.intellij.java", "org.jetbrains.kotlin"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
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
