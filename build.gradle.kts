import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    java
}

group = "org.enginehub.worldgourd"
version = "1.2.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven { url = uri("https://maven.enginehub.org/repo/") }
}

dependencies {
    compileOnly("dev.folia:folia-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0")
}

tasks.named<Copy>("processResources") {
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}
