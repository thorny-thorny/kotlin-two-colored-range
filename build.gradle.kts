import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    kotlin("multiplatform") version "1.9.20"
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.29.0"
}

group = "me.thorny"
version = "0.9.2"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        java.sourceCompatibility = JavaVersion.VERSION_1_8
        java.targetCompatibility = JavaVersion.VERSION_1_8
        testRuns["test"].executionTask.configure {
            testLogging {
                events(
                    org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                    org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
                )
            }
        }
    }
    js {
        browser {
            testTask {
                useKarma {
                    useSafari()
                }
            }
        }
        nodejs()
    }
    macosX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmTest by getting
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(project.group as String, "two-colored-range", project.version as String)

    configure(KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaHtml"),
        sourcesJar = true,
    ))

    pom {
        name.set("Two colored range")
        description.set("Two colored range data structure")
        inceptionYear.set("2021")
        url.set("https://github.com/thorny-thorny/two-colored-range")
        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/license/MIT")
                distribution.set("https://opensource.org/license/MIT")
            }
        }
        developers {
            developer {
                id.set("thorny")
                name.set("Thorny")
                url.set("https://thorny.me")
                email.set("thorny.develops@gmail.com")
            }
        }
        scm {
            url.set("https://github.com/thorny-thorny/two-colored-range")
            connection.set("scm:git:git://github.com/thorny-thorny/two-colored-range.git")
            developerConnection.set("scm:git:ssh://git@github.com/thorny-thorny/two-colored-range.git")
        }
    }
}

kover {
    useJacoco()
}
