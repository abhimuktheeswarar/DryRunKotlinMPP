import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    //id("com.prof18.kmp.fatframework.cocoa") version "0.2.1"
    id("com.android.library")
    id("org.jetbrains.dokka") version Versions.dokka
    id("maven-publish")
    id("signing")
}

group = "com.msabhi"
version = "1.0.5-RC"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    android {
        publishAllLibraryVariants()
    }
    js(BOTH) {
        browser()
        nodejs()
    }
    ios()
    watchos()
    tvos()
    macosX64()
    linuxX64()
    mingwX64()
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
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect", Versions.kotlin))
            }
        }
        val jvmTest by getting {
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
        val androidMain by getting {
            dependencies {
                implementation(kotlin("reflect", Versions.kotlin))
                implementation(Dependencies.Coroutines.android)
                implementation(Dependencies.Android.lifecycleRuntime)
                implementation(Dependencies.Android.lifecycleViewModel)
            }
        }
        val androidTest by getting {
            dependsOn(jvmTest)
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js", Versions.kotlin))
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val nativeTest by creating {
            dependsOn(commonTest)
        }
        val appleMain by creating {
            dependsOn(commonMain)
        }
        val appleTest by creating {
            dependsOn(commonTest)
        }
        val iosMain by getting {
            dependsOn(appleMain)
        }
        val iosTest by getting {
            dependsOn(appleTest)
        }
        val watchosMain by getting {
            dependsOn(appleMain)
        }
        val watchosTest by getting {
            dependsOn(appleTest)
        }
        val tvosMain by getting {
            dependsOn(appleMain)
        }
        val tvosTest by getting {
            dependsOn(appleTest)
        }
        val macosX64Main by getting {
            dependsOn(appleMain)
        }
        val macosX64Test by getting {
            dependsOn(appleTest)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val linuxX64Test by getting {
            dependsOn(nativeTest)
        }
        val mingwX64Main by getting {
            dependsOn(nativeMain)
        }
        val mingwX64Test by getting {
            dependsOn(nativeTest)
        }
        /*cocoapods {
            // Configure fields required by CocoaPods.
            summary = "DryRunKotlinMPP Kotlin/Native module CocoaPods"
            homepage = "https://github.com/abhimuktheeswarar/DryRunKotlinMPP"
        }*/
    }
}

android {
    compileSdkVersion(Versions.Android.compileSdk)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)
    }
    //Sample
    dependencies {
        debugImplementation(kotlin("reflect", Versions.kotlin))
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

tasks.dokkaGfm.configure {
    outputDirectory.set(rootDir.resolve("docs"))
}

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
                        name.set(gradleLocalProperties(
                            rootDir).getProperty("developerName",
                            System.getenv("DEVELOPER_NAME")))
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

//----------------------------------------------------------------------------------

val xcFrameworkPath = "xcframework/${project.name}.xcframework"

tasks.create<Delete>("deleteXcFramework") { delete = setOf(xcFrameworkPath) }

val buildXcFramework by tasks.registering {
    dependsOn("deleteXcFramework")
    group = "build"
    val mode = "Release"
    val frameworks = arrayOf(
        "iosArm64",
        "iosX64",
        "watchosArm64",
        "watchosX64",
        "tvosArm64",
        "tvosX64",
        "macosX64")
        .map { kotlin.targets.getByName<KotlinNativeTarget>(it).binaries.getFramework(mode) }
    inputs.property("mode", mode)
    dependsOn(frameworks.map { it.linkTask })
    doLast { buildXcFramework(frameworks) }
}

fun Task.buildXcFramework(frameworks: List<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>) {
    val buildArgs: () -> List<String> = {
        val arguments = mutableListOf("-create-xcframework")
        frameworks.forEach {
            arguments += "-framework"
            arguments += "${it.outputDirectory}/${project.name}.framework"
        }
        arguments += "-output"
        arguments += xcFrameworkPath
        arguments
    }
    exec {
        executable = "xcodebuild"
        args = buildArgs()
    }
}

//----------------------------------------------------------------------------------

multiplatformSwiftPackage {
    swiftToolsVersion("5.3")
    packageName(project.name)
    outputDirectory(File(projectDir, "swiftpackage"))
    targetPlatforms {
        iOS { v("5") }
        tvOS { v("9") }
        macOS { v("10") }
        //watchOS { v("4") }
        //targets("watchosArm64") { v("4") }
        //targets("watchosX86") { v("4") }
        //targets("watchosX64") { v("4") }

    }
}