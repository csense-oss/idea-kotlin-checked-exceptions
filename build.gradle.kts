plugins {
    id("org.jetbrains.intellij") version "0.4.19"
    kotlin("jvm") version "1.3.72"
    java
    id("org.owasp.dependencycheck") version "5.2.4"
}

group = "csense-idea"
version = "0.9.12"

intellij {
    updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
    setPlugins("Kotlin","java")
    version = "2019.2"
}


repositories {
    jcenter()
    maven(url = "https://dl.bintray.com/csense-oss/maven")
    maven(url = "https://dl.bintray.com/csense-oss/idea")
}

dependencies {
    implementation("csense.kotlin:csense-kotlin-jvm:0.0.33")
    implementation("csense.kotlin:csense-kotlin-annotations-jvm:0.0.17")
    implementation("csense.kotlin:csense-kotlin-ds-jvm:0.0.24")
    implementation("csense.idea.base:csense-idea-base:0.1.9")
}


tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        <ul>
            <li>callthough.ignore bug fixed (it would not respect annotated methods, only try catches)</li>
            <li>callthough.ignore bug fixed if there are multiple lambdas in eachother and some were marked callthough</li>
        </ul>
      """)
}

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    this.kotlinOptions.jvmTarget = "1.8"
}

java{
    this.sourceCompatibility = JavaVersion.VERSION_1_8
    this.targetCompatibility = JavaVersion.VERSION_1_8
}
