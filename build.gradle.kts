plugins {
    //https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.4.0"
    kotlin("jvm") version "1.6.10"
    java
    //https://github.com/jeremylong/dependency-check-gradle/releases
    id("org.owasp.dependencycheck") version "6.5.3"
}

group = "csense-idea"
version = "1.1.5"

intellij {
    updateSinceUntilBuild.set(false)
    plugins.set(listOf("Kotlin", "java"))
//    version.set("2020.3")
    version.set("2021.3.2")
}




repositories {
    mavenCentral()
    maven {
        setUrl("https://pkgs.dev.azure.com/csense-oss/csense-oss/_packaging/csense-oss/maven/v1")
        name = "Csense oss"
    }
}

dependencies {
    implementation("csense.kotlin:csense-kotlin-jvm:0.0.55")
    implementation("csense.kotlin:csense-kotlin-annotations-jvm:0.0.50")
    implementation("csense.kotlin:csense-kotlin-datastructures-algorithms:0.0.41")
    implementation("csense.idea.base:csense-idea-base:0.1.41")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation("csense.kotlin:csense-kotlin-tests:0.0.55")
    testImplementation("csense.idea.test:csense-idea-test:0.1.0")
}


tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set(
        """
        <ul>
            <li>Disabled (by default)"Throws inside of function" annotation, as it is sometimes right and sometimes wrong. but its also quite annoying. It can be turned on in the settings iff need be :) (it has now its own settings as well)</li>
            <li> Did improve the "Throws inside of function" annotation to link to documentation and explain inline functions + lambdas returning nothing.
            <li>Fixed some issues with android studio compatibility </li>
            <li>Fixed numerous bugs</li>
        </ul>
      """
    )
}

/*
*
*   <li>Fixed "Mark function as Throws" when inside a lambda (The annotation got placed at the lambda rather than at the function declaration)</li>
            <li>Added most of kotlin std lib functions as either callthough or ignore</li>
            <li>Fixed issues with java interopt</li>
            <li>Fixed issues with Throwable</li>
            <li>Fixed issues with indirect exceptions (via variables & method calls)</li>
*
* */

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

java {
    this.sourceCompatibility = JavaVersion.VERSION_1_8
    this.targetCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
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
