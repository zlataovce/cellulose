plugins {
    java
}

allprojects {
    group = "me.kcra.cellulose"
    version = "0.0.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
    }
}