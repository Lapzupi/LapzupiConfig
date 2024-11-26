plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.lapzupi.dev.config"
version = "1.2.0"

dependencies {
    compileOnly(libs.paper.api)
    
    //libraries
    api(libs.configurate.yaml)
    api(libs.configurate.gson)
    api(libs.configurate.hocon)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withSourcesJar()
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


