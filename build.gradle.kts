plugins {
    id("org.jetbrains.intellij") version "0.4.21"
    kotlin("jvm") version "1.3.72"
    java
    id("org.owasp.dependencycheck") version "5.3.2"
}

group = "csense-idea"
version = "1.0.1"

intellij {
    updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
    setPlugins("Kotlin", "java")
    version = "2019.2"
}


repositories {
    jcenter()
    maven(url = "https://dl.bintray.com/csense-oss/maven")
    maven(url = "https://dl.bintray.com/csense-oss/idea")
}

dependencies {
    implementation("csense.kotlin:csense-kotlin-jvm:0.0.36")
    implementation("csense.kotlin:csense-kotlin-annotations-jvm:0.0.18")
    implementation("csense.kotlin:csense-kotlin-ds-jvm:0.0.25")
    implementation("csense.idea.base:csense-idea-base:0.1.19")
}


tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        <ul>
            <li>Updated icons to svg and not so bright colors</li>
            <li>Removed some deprecated code</li>
        </ul>
      """)
}

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class) {
    this.kotlinOptions.jvmTarget = "1.8"
}

java {
    this.sourceCompatibility = JavaVersion.VERSION_1_8
    this.targetCompatibility = JavaVersion.VERSION_1_8
}
