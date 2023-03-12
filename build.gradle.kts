plugins {
    id("java")
    id("maven-publish")
}

group = "com.lapzupi.dev"
version = "1.1.0"

repositories {
    mavenCentral()
    maven(
        url = "https://repo.papermc.io/repository/maven-public/"
    )
    maven(
        url = "https://oss.sonatype.org/content/groups/public/"
    )
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    
    //libraries
    compileOnly("org.spongepowered:configurate-yaml:4.1.2")
    compileOnly("org.spongepowered:configurate-gson:4.1.2")
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
    
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}



publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = groupId
            artifactId = artifactId
            version = version
            
            from(components["java"])
        }
    }
}


