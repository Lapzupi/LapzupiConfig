plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.lapzupi.dev"
version = "1.1.2"

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
    compileOnly(libs.paper.api)
    
    //libraries
    api(libs.configurate.yaml)
    api(libs.configurate.gson)
    api(libs.configurate.hocon)
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


