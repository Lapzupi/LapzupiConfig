rootProject.name = "config"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
    }
    versionCatalogs {
        create("libs") {
            version("configurate", "4.2.0-SNAPSHOT")
            library("configurate-hocon", "org.spongepowered","configurate-hocon").versionRef("configurate")
            library("configurate-gson", "org.spongepowered","configurate-gson").versionRef("configurate")
            library("configurate-yaml", "org.spongepowered","configurate-yaml").versionRef("configurate")
            library("paper-api", "io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
        }
    }
}