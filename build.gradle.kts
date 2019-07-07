plugins {
    id("org.jetbrains.intellij") version "0.4.9"
    kotlin("jvm") version "1.3.41"
    java
    id("org.owasp.dependencycheck") version "5.1.0"
}

group = "csense-idea"
version = "0.8"

intellij {
    updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
    setPlugins("kotlin")
    version = "2018.3.1"
}

repositories {
    jcenter()
}

dependencies {
    compile("csense.kotlin:csense-kotlin-jvm:0.0.19")
//    runtime("org.jetbrains.kotlin:kotlin-compiler-embeddable")
//    compile 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.2'
//    compile 'org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.2.2'
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        Fixed issue with quickfix and mark function as throws.
      """)
}

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")
