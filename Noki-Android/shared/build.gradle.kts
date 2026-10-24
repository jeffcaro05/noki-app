plugins { id("org.jetbrains.kotlin.multiplatform") }

kotlin {
    jvm { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    iosArm64 { binaries.framework { baseName = "NokiCore"; isStatic = true } }
    iosSimulatorArm64 { binaries.framework { baseName = "NokiCore"; isStatic = true } }
    sourceSets {
        commonTest.dependencies { implementation(kotlin("test")) }
    }
}
