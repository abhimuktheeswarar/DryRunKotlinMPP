import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka") version Versions.dokka
    id("maven-publish")
    id("signing")
}

group = "com.msabhi"
version = "1.0.0-SNAPSHOT"

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }
    ios {
        binaries {
            framework {
                baseName = "dryrun"
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Dependencies.Coroutines.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Dependencies.KotlinTest.common)
                implementation(Dependencies.KotlinTest.annotations)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(kotlin("reflect", Versions.kotlin))
                implementation(Dependencies.Coroutines.android)
                implementation(Dependencies.Android.lifecycleRuntime)
                implementation(Dependencies.Android.lifecycleViewModel)
            }

        }

        val androidTest by getting {
            dependencies {
                implementation(Dependencies.KotlinTest.jvm)
                implementation(Dependencies.KotlinTest.junit)
                implementation(Dependencies.Coroutines.test)
                implementation(Dependencies.AndroidTest.core)
                implementation(Dependencies.AndroidTest.junit)
                implementation(Dependencies.AndroidTest.runner)
                implementation(Dependencies.AndroidTest.rules)
            }
        }
        val iosMain by getting
        val iosTest by getting
    }
}

android {
    compileSdkVersion(Versions.Android.compileSdk)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val sdkName = System.getenv("SDK_NAME") ?: "iphonesimulator"
    val targetName = "ios" + if (sdkName.startsWith("iphoneos")) "Arm64" else "X64"
    val framework =
        kotlin.targets.getByName<KotlinNativeTarget>(targetName).binaries.getFramework(mode)
    inputs.property("mode", mode)
    dependsOn(framework.linkTask)
    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)
}

tasks.getByName("build").dependsOn(packForXcode)

//----------------------------------------------------------------------------------

val dokkaOutputDir = "$buildDir/docs"

tasks.dokkaHtml.configure {
    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

publishing {
    repositories {
        maven {
            name = "sonatype"
            setUrl {

                val releasesRepoUrl =
                    "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl =
                    "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            }
            credentials {
                username = gradleLocalProperties(
                    rootDir).getProperty("sonatypeUsername",
                    System.getenv("SONATYPE_USERNAME"))
                password = gradleLocalProperties(
                    rootDir).getProperty("sonatypePassword",
                    System.getenv("SONATYPE_PASSWORD"))
            }
        }
    }

    publications {

        withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name.set("DryRunKotlinMMPP")
                description.set("Kotlin-Multiplatform state management library")
                url.set("https://github.com/abhimuktheeswarar/DryRunKotlinMPP")
                inceptionYear.set("2021")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                issueManagement {
                    system.set("Github issues")
                    url.set("https://github.com/abhimuktheeswarar/DryRunKotlinMPP/issues")
                }
                scm {
                    connection.set("https://github.com/abhimuktheeswarar/DryRunKotlinMPP.git")
                    url.set("https://github.com/abhimuktheeswarar/DryRunKotlinMPP")
                }
                developers {
                    developer {
                        id.set(gradleLocalProperties(
                            rootDir).getProperty("developerId",
                            System.getenv("DEVELOPER_ID")))
                        name.set("Abhi Muktheeswarar")
                        email.set(gradleLocalProperties(
                            rootDir).getProperty("developerEmail",
                            System.getenv("DEVELOPER_EMAIL")))
                    }
                }
            }
        }
    }
}

signing {
    val localProps = gradleLocalProperties(rootDir)
    val signingKey = localProps.getProperty("signing.key", System.getenv("SIGNING_KEY"))
    val signingPassword = localProps.getProperty(
        "signing.password",
        System.getenv("SIGNING_PASSWORD")
    )
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}