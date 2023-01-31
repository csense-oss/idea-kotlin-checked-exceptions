plugins {
    //https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.12.0"
    kotlin("jvm") version "1.8.0"
    //https://github.com/jeremylong/dependency-check-gradle/releases
    id("org.owasp.dependencycheck") version "8.0.2"
}

group = "csense-idea"
version = "1.3.0"

intellij {
    updateSinceUntilBuild.set(false)
    plugins.set(listOf("Kotlin", "java"))
    version.set("2021.3")
}


repositories {
    mavenCentral()
    maven {
        setUrl("https://pkgs.dev.azure.com/csense-oss/csense-oss/_packaging/csense-oss/maven/v1")
        name = "Csense oss"
    }
}

dependencies {
    implementation("csense.kotlin:csense-kotlin-jvm:0.0.59")
    implementation("csense.kotlin:csense-kotlin-annotations-jvm:0.0.50")
    implementation("csense.kotlin:csense-kotlin-datastructures-algorithms:0.0.41")
    implementation("csense.idea.base:csense-idea-base:0.1.60")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("csense.kotlin:csense-kotlin-tests:0.0.59")
    testImplementation("csense.idea.test:csense-idea-test:0.3.0")
}


tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """
        <ul>
            <li></li>
        </ul>
      """
    )
}


tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    test {
        testLogging {
            showExceptions = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }
    buildSearchableOptions {
        enabled = false
    }
    runIde {
//        ideDir.set(file("/home/kasper/.local/share/JetBrains/Toolbox/apps/AndroidStudio/ch-0/211.7628.21.2111.8139111/"))
    }
}
sourceSets {
    test {
        resources {
            srcDir("testData")
        }
    }
}
