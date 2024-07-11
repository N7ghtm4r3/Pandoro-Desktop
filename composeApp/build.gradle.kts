import org.jetbrains.compose.desktop.application.dsl.TargetFormat.*
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "18"
        }
        withJava()
    }
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
            implementation("com.github.N7ghtm4r3:Equinox:1.0.1")
            implementation("com.github.N7ghtm4r3:APIManager:2.2.3")
            implementation("com.tecknobit.pandorocore:Pandoro-core:1.0.4")
            implementation("com.github.N7ghtm4r3:OctocatKDU:1.0.3")
            implementation("com.netguru.multiplatform-charts:multiplatform-charts-desktop:1.0.0")
            implementation("org.json:json:20230227")
            implementation("com.darkrockstudios:mpfilepicker:3.1.0")
            api("commons-validator:commons-validator:1.7")
            api("moe.tlaster:precompose:1.5.11")
            implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha07")
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0-alpha07")
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}


compose.desktop {
    application {
        mainClass = "LauncherKt"
        nativeDistributions {
            targetFormats(Deb, Pkg, Exe)
            modules(
                "java.compiler", "java.instrument", "java.management", "java.net.http", "java.prefs", "java.rmi",
                "java.scripting", "java.security.jgss", "java.sql.rowset", "jdk.jfr", "jdk.unsupported"
            )
            packageName = "Pandoro"
            packageVersion = "1.0.4"
            version = "1.0.4"
            description = "Pandoro, open source management software"
            copyright = "© 2024 Tecknobit"
            vendor = "Tecknobit"
            licenseFile.set(project.file("LICENSE"))
            macOS {
                bundleID = "com.tecknobit.pandoro"
                iconFile.set(project.file("src/desktopMain/resources/icons/logo.icns"))
            }
            windows {
                iconFile.set(project.file("src/desktopMain/resources/icons/logo.ico"))
                upgradeUuid = UUID.randomUUID().toString()
            }
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icons/logo.icns"))
                packageName = "com-tecknobit-pandoro"
                debMaintainer = "infotecknobitcompany@gmail.com"
                appRelease = "1.0.4"
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
