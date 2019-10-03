plugins {
    id("org.jetbrains.intellij") version "0.4.10"
    kotlin("jvm") version "1.3.50"
    java
    id("org.owasp.dependencycheck") version "5.1.0"
}

group = "csense-idea"
version = "0.9.4"

intellij {
    updateSinceUntilBuild = false //Disables updating since-build attribute in plugin.xml
    setPlugins("kotlin")
    version = "2018.2"
}


repositories {
    jcenter()
}

dependencies {
    compile("csense.kotlin:csense-kotlin-jvm:0.0.21")
    compile("csense.kotlin:csense-kotlin-ds-jvm:0.0.21")
}


tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
        <ul>
            <li>Nothing functions (fail & test &  assert ) in test modules will not be annotated.</li>
        </ul>
      """)
}

tasks.getByName("check").dependsOn("dependencyCheckAnalyze")
