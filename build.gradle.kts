import org.jetbrains.intellij.platform.gradle.*

plugins {
    //https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij.platform") version "2.2.1"
    //https://github.com/JetBrains/kotlin
    kotlin("jvm") version "2.1.0"
    //https://jeremylong.github.io/DependencyCheck/
    id("org.owasp.dependencycheck") version "12.0.0"
}

repositories {
    intellijPlatform {
        defaultRepositories()
    }
}

val javaVersion = "17"

group = "csense-idea"
version = "2.2.1"


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
    implementation("csense.idea.base:csense-idea-base:0.1.71")
    //https://github.com/Kotlin/kotlinx.serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    //https://github.com/Kotlin/kotlinx.coroutines
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    //https://github.com/csense-oss/csense-kotlin-test
    testImplementation("csense.kotlin:csense-kotlin-tests:0.0.60")
    //https://github.com/csense-oss/csense-oss-idea-kotlin-shared-test
    testImplementation("csense.idea.test:csense-idea-test:0.3.0")


    intellijPlatform {
        intellijIdeaCommunity("2022.3")

        bundledPlugin("org.jetbrains.kotlin")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
        testFramework(TestFrameworkType.Platform)

    }
}
intellijPlatform {
    pluginConfiguration {
        //language=html
        changeNotes = """
            <ul>
               <li>ignore process cancellation exception (https://github.com/csense-oss/idea-kotlin-checked-exceptions/issues/40)</li>
               <li>fixed some deprecated type resolution(s)</li>
            </ul>
        """.trimIndent()
        ideaVersion {
            sinceBuild = "223"
            untilBuild = provider { null }
        }
    }
}

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

tasks {

    withType<JavaCompile> {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    test {
        testLogging {
            showExceptions = true
            showStackTraces = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
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