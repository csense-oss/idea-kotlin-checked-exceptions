plugins {
    id("org.jetbrains.intellij") version "0.4.9"
    kotlin("jvm") version "1.3.40"
}

group = "csense-idea"
version = "1.0-SNAPSHOT"

intellij {
    updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
    setPlugins("org.jetbrains.kotlin:1.3.11-release-IJ2018.3-1")
    version = "2018.3.1"
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib"))
    compile("csense.kotlin:csense-kotlin-jvm:0.0.17")
//    runtime("org.jetbrains.kotlin:kotlin-compiler-embeddable")
//    compile 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.2'
//    compile 'org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.2.2'
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}