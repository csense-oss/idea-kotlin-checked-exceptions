plugins {
//https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.13.3"
    //https://github.com/JetBrains/kotlin
    kotlin("jvm") version "1.8.21"
    //https://jeremylong.github.io/DependencyCheck/
    id("org.owasp.dependencycheck") version "8.2.1"
}

val javaVersion = "11"

group = "csense-idea"
version = "2.0.0"

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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    //https://github.com/Kotlin/kotlinx.coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.0")
    //https://github.com/csense-oss/csense-kotlin-test
    testImplementation("csense.kotlin:csense-kotlin-tests:0.0.60")
    //https://github.com/csense-oss/csense-oss-idea-kotlin-shared-test
    testImplementation("csense.idea.test:csense-idea-test:0.3.0")
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """
        <ul>
          <li>rewrote most of the code to fix issues</li> 
          <li>Most features are now working across android studio & / intellij and across various versions</li>
          <li>Performance should be superb</li>
          <li>Quick fixes should generally work pretty well now</li>
          <li>a lot of things that previously did not work (or only partially worked) now works</li>
          <li>Highlights simple Throws in kotlin doc</li>
          <li>should respect imports now</li>
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
