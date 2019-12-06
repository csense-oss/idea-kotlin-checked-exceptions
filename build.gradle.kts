plugins {
    id("org.jetbrains.intellij") version "0.4.14"
    kotlin("jvm") version "1.3.61"
    java
    id("org.owasp.dependencycheck") version "5.1.0"
}

group = "csense-idea"
version = "0.9.8"

intellij {
    updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
    setPlugins("Kotlin","java")
    version = "2019.2"
}


repositories {
    jcenter()
    maven { url = uri("https://dl.bintray.com/csense-oss/maven") }
}

dependencies {
    compile("csense.kotlin:csense-kotlin-jvm:0.0.26")
    compile("csense.kotlin:csense-kotlin-annotations-jvm:0.0.11")
    compile("csense.kotlin:csense-kotlin-ds-jvm:0.0.24")
}


tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        <ul>
            <li>Fixed bug with java throws not being read correctly.</li>
        </ul>
      """)
}

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    this.kotlinOptions.jvmTarget = "1.8"
}
