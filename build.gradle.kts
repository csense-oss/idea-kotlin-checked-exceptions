plugins {
    //https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.15.0"
    //https://github.com/JetBrains/kotlin
    kotlin("jvm") version "1.9.10"
    //https://jeremylong.github.io/DependencyCheck/
    id("org.owasp.dependencycheck") version "8.4.0"
}

val javaVersion = "11"

group = "csense-idea"
version = "2.0.1"

intellij {
    updateSinceUntilBuild.set(false)
    plugins.set(listOf("Kotlin", "java"))
    version.set("2021.3")
}


repositories {
    mavenCentral()
    mavenLocal()
    maven {
        setUrl("https://pkgs.dev.azure.com/csense-oss/csense-oss/_packaging/csense-oss/maven/v1")
        name = "Csense oss"
    }
}


dependencies {
    //https://github.com/csense-oss/csense-kotlin
    implementation("csense.kotlin:csense-kotlin-jvm:0.0.60")
    //https://github.com/csense-oss/csense-kotlin-annotations
    implementation("csense.kotlin:csense-kotlin-annotations-jvm:0.0.63")
    //https://github.com/csense-oss/idea-kotlin-shared-base
    implementation("csense.idea.base:csense-idea-base:0.1.60")
    //https://github.com/Kotlin/kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    //https://github.com/Kotlin/kotlinx.coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    //https://github.com/csense-oss/csense-kotlin-test
    testImplementation("csense.kotlin:csense-kotlin-tests:0.0.60")
    //https://github.com/csense-oss/csense-oss-idea-kotlin-shared-test
    testImplementation("csense.idea.test:csense-idea-test:0.3.0")
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """
        <ul>
          <li> Fixed a regression where the main inspection was not enabled by default</li>
          <li> And in relation made sure that the main inspection default highlight level is warning</li>
          <li> Fixed main inspection description</li>
          <li> Bumped 3 party dependencies</li>
        </ul>
        
      """
    )
}


tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

tasks {

    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersion
    }

    test {
        testLogging {
            showExceptions = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    runIde {
//        ideDir.set(file("/home/kasper/.local/share/JetBrains/Toolbox/apps/AndroidStudio/ch-0/211.7628.21.2111.8139111/"))
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

sourceSets {
    test {
        resources {
            srcDir("testData")
        }
    }
}
