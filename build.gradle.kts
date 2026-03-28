import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
    signing
    jacoco
}

group = "app.silva"

tasks {
    processResources {
        expand("projectVersion" to project.version)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("PASSED", "SKIPPED", "FAILED")
        }
        finalizedBy(jacocoTestReport)
        testLogging { exceptionFormat = TestExceptionFormat.FULL }
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
    // Obtain baksmali/smali from source builds - https://github.com/iBotPeaches/smali
    // Remove when official smali releases come out again.
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.iBotPeaches.smali")
            includeGroup("com.github.MorpheApp")
        }
    }
    maven {
        // A repository must be specified for some reason. "registry" is a dummy.
        url = uri("https://maven.pkg.github.com/MorpheApp/registry")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/SilvaTechB/silva-library")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    compileOnly(libs.android) {
        // Exclude, otherwise the org.w3c.dom API breaks.
        exclude(group = "xerces", module = "xmlParserAPIs")
    }

    implementation(libs.silva.library.jvm)

    implementation(libs.bcpkix.jdk18on)
    implementation(libs.apktool.lib)
    implementation(libs.apksig)
    implementation(libs.apkzlib)
    implementation(libs.arsclib)
    implementation(libs.guava)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.multidexlib2)
    implementation(libs.smali)

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)

        freeCompilerArgs = listOf("-Xcontext-receivers")
    }
}

tasks.withType<Test> {
    testLogging {
        // Uncomment to show println and exception stack traces in unit tests.
        // showStandardStreams = true
    }
}


java {
    targetCompatibility = JavaVersion.VERSION_11

    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/SilvaTechB/silva-patcher")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("silva-patcher-publication") {
            from(components["java"])

            groupId = "app.silva"
            artifactId = "silva-patcher"
            version = project.version.toString()

            pom {
                name = "Silva Patcher"
                description = "Patcher used by Silva."
                url = "https://github.com/SilvaTechB/silva-patcher"

                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.en.html"
                        comments = "Additional conditions under GPL section 7 apply: Project name restrictions. See LICENSE and NOTICE file."
                    }
                }
                developers {
                    developer {
                        id = "SilvaTechB"
                        name = "SilvaTechB"
                        email = "contact@silva.software"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/SilvaTechB/silva-patcher.git"
                    developerConnection = "scm:git:git@github.com:SilvaTechB/silva-patcher.git"
                    url = "https://github.com/SilvaTechB/silva-patcher"
                }
            }
        }
    }
}

signing {
    val gpgKey = System.getenv("GPG_PRIVATE_KEY")
    val gpgPassphrase = System.getenv("GPG_PASSPHRASE")
    if (!gpgKey.isNullOrBlank() && !gpgPassphrase.isNullOrBlank()) {
        useInMemoryPgpKeys(gpgKey, gpgPassphrase)
        sign(publishing.publications["silva-patcher-publication"])
    }
}
