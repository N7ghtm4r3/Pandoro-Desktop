import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.tecknobit"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    // TODO: REMOVE FOR TESTING PURPOSES ONLY
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "19"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                api(compose.foundation)
                api(compose.animation)
                api(compose.materialIconsExtended)
                implementation("com.github.N7ghtm4r3:APIManager:2.1.7")
                implementation("com.tecknobit.pandoro:Pandoro:0.0.0.1")
                implementation("com.netguru.multiplatform-charts:multiplatform-charts-desktop:1.0.0")
                implementation("org.json:json:20230227")
                implementation("com.darkrockstudios:mpfilepicker:2.0.2")
                api("commons-validator:commons-validator:1.7")
                api("moe.tlaster:precompose:1.3.14")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "LauncherKt"
        nativeDistributions {
            targetFormats(Deb, Rpm, Pkg, Exe)
            packageName = "Pandoro"
            packageVersion = "${rootProject.version}"
            version = "${rootProject.version}"
            description = "Pandoro, open source management software"
            copyright = "© 2023 Tecknobit"
            vendor = "Tecknobit"
            licenseFile.set(project.file("LICENSE"))
            macOS {
                bundleID = "com.tecknobit.pandoro"
                //iconFile.set(project.file("icons/logo.icns"))
            }
            windows {
                //iconFile.set(project.file("icons/logo.ico"))
                upgradeUuid = UUID.randomUUID().toString()
            }
            linux {
                //iconFile.set(project.file("icons/logo.png"))
                packageName = "com-tecknobit-pandoro"
                debMaintainer = "infotecknobitcompany@gmail.com"
                appRelease = "1.0.0"
                appCategory = "PERSONALIZATION"
                rpmLicenseType = "MIT"
            }
        }
        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
            obfuscate.set(true)
        }
    }
}

configurations.all {
    exclude("commons-logging", "commons-logging")
}