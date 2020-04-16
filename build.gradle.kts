import com.google.protobuf.gradle.*

plugins {
    `java-library`
    `maven-publish`
    id("com.google.protobuf") version "0.8.8"
}

repositories {
    maven(url = "https://maven-central.storage-download.googleapis.com/repos/central/data/")
    mavenCentral()
}
group = "ru.yandex.cloud"
version = "1.0.0"

val grpcVersion = "1.28.1"
val protobufVersion = "3.11.4"
val protocVersion = protobufVersion

dependencies {
    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    compileOnly("javax.annotation:javax.annotation-api:1.3.1")
    implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")
    runtimeOnly("io.grpc:grpc-netty-shaded:${grpcVersion}")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protocVersion}"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/grpc")
            srcDir("build/generated/source/proto/main/java")
        }
    }
}

tasks.withType<Jar> {
    from(sourceSets["main"].allSource)
    configurations.runtimeClasspath.get().filter {
        it.name.endsWith(".jar")
    }.forEach { jar ->
        from(zipTree(jar))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["java"])
        }
    }
}
