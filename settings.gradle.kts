rootProject.name = "config"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("configurate", "4.1.2")
            library("configurate-hocon", "org.spongepowered","configurate-hocon").versionRef("configurate")
            library("configurate-gson", "org.spongepowered","configurate-gson").versionRef("configurate")
            library("configurate-yaml", "org.spongepowered","configurate-yaml").versionRef("configurate")
            library("paper-api", "io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
        }
    }
}